package com.parser.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.file.FileUtil;
import com.iii360.sup.common.utl.LogManager;
import com.parser.command.AbstractCommandParser;
import com.parser.command.CommandParserFactory;
import com.parser.iengine.crf.CRFUtil;

public class AutoTest {
    
    public static class TestInfo {
        public TestInfo(String commandName, ResultChecker checker) {
            _commandName = commandName;
            _checker     = checker;
        }
        public String _commandName;
        public ResultChecker _checker;
    }
    
    
    
    private static OnTestCompletedListener mListener;
    private AutoTest(){};
    
    public static interface OnTestCompletedListener {
        public void onTestCompleted(TestResult result);
    }
    
    /**
     * 初始化测试环境
     */
    public static void initTest(OnTestCompletedListener l, Context context) {
        mListener = l;
        CRFUtil.init(context, context.getPackageName());
    }
    
    public static void runIntegrationTesting(final Context context, final ArrayList<TestInfo> infoList, final boolean isReGenTestData, final boolean isNeedTrain) {
//        ArrayList<String> matchList = new ArrayList<String>();
//        ArrayList<String> errorList = new ArrayList<String>();
//        ArrayList<String> noMatchList = new ArrayList<String>();
//        ArrayList<String> testData = getTestDataList("Integration");
        
        LogManager.d("=========Run Integration Test:" + " Start!");
        if(infoList != null) {
            new Thread() {
                public void run() {
                    for(TestInfo info : infoList) {
                        TestResult result = testCommandParser(context, info._commandName, info._checker, isReGenTestData, isNeedTrain);
                        if(mListener != null) {
                            mListener.onTestCompleted(result);
                        }
                    }

                    
                }
            }.start();   
        }

        
    }

    /**
     * @param commandName 待测试的命令名称
     * @param checker     结果判定器(传入null)
     * @param isReGenTestData 是否需要重新生成测试输入文件。第一次运行时请传入true,后边如果没有修改字典和模板文件，请传入false
     * @param isNeedTrain 是否需要重新生成训练文件(如果没有使用CRF，传入false)
     */
    public static void runTestParser(final Context context, final String commandName, final ResultChecker checker, final boolean isReGenTestData, final boolean isNeedTrain) {
        new Thread() {
            public void run() {
                TestResult result = testCommandParser(context, commandName, checker, isReGenTestData, isNeedTrain);
                if(mListener != null) {
                    mListener.onTestCompleted(result);
                }
                
            }
        }.start();
        
    }
    
    /**
     * @param commandName 待测试的命令名称
     * @param checker     结果判定器(传入null)
     * @param isReGenTestData 是否需要重新生成测试输入文件。第一次运行时请传入true,后边如果没有修改字典和模板文件，请传入false
     * @param isNeedTrain     是否需要重新生成训练文件
     */
    public static void runTestCRF(final String commandName, final ResultChecker checker, boolean isReGenTestData, final boolean isNeedTrain) {
        if(isReGenTestData) {
            AutoTest.generationTestData(commandName);
        }
        
        new Thread() {
            public void run(Context context) {
                
                TestResult result = AutoTest.testCRF(commandName, checker, isNeedTrain, context);
                if(mListener != null) {
                    mListener.onTestCompleted(result);
                }
                
            }
        }.start();
    }
    
    /**
     * @param commandName 待测试的命令名称
     * @param checker     结果判定器(传入null)
     * @param isReGenTestData 是否需要重新生成测试输入文件。第一次运行时请传入true,后边如果没有修改字典和模板文件，请传入false
     */
    public static final TestResult testCommandParser(Context context, String commandName, ResultChecker checker, boolean isReGenTestData, final boolean isNeedTrain) {
        LogManager.d("=========Test CommandParser:" + commandName + " Start!");

        
        if(isReGenTestData) {
            generationTestData(commandName);
        }
       
        if(isNeedTrain) {
            CRFUtil.training(commandName);
        }
        
        ArrayList<String> matchList = new ArrayList<String>();
        ArrayList<String> errorList = new ArrayList<String>();
        ArrayList<String> noMatchList = new ArrayList<String>();
        ArrayList<String> testData = getTestDataList(commandName);
        
        long start = System.currentTimeMillis();
        
        for(String data : testData) {
            String[] inputs = data.split(" ");
            String output = inputs[0];
            
            AbstractCommandParser parser = CommandParserFactory.makeParser(output);
            
            if(parser != null && parser.getCommandName().equals(commandName)) {
            	CommandInfo info = parser.parser();
                
                if(info != null) {
                    ArrayList<String> args = info.getArgList();
                    if(args != null) {
                        for(int index= 0;index < args.size();index++) {
                            String arg = args.get(index);
                            if(arg != null && !arg.equals("")) {
                                output += " " + "arg" + index + ":" + arg;
                            }
                            
                        } 
                    }
       
                    
                    if(checker != null) {
                        String recovery = checker.recovery(data, output);
                        if(recovery == null) {
                            matchList.add(output);
                        } else {
                            errorList.add(recovery);
                        }
                    } else {
                        matchList.add(output);
                    }
                    
                } else {
                    noMatchList.add(output);
                }
                

            } else {
                noMatchList.add(output);
            }
        }
        
        long end = System.currentTimeMillis();
        long runTime = end - start;
        
        TestResult result = new TestResult(commandName, runTime, testData.size(), matchList.size());
        TestResult errorResult = new TestResult(commandName, runTime, testData.size(), errorList.size());
        TestResult noMatchResult = new TestResult(commandName, runTime, testData.size(), noMatchList.size());
        
        outputTestResult(matchList, commandName, FileUtil.TRIM_MATCH, result);
        outputTestResult(noMatchList, commandName, FileUtil.TRIM_NOMATCH, noMatchResult);
        outputTestResult(errorList, commandName, FileUtil.TRIM_ERRORMATCH, errorResult);
        LogManager.d("=========Test CommandParser:" + commandName + " Done!");
        
        return result;
    }
    
    private static ArrayList<String> getTestDataList(String commandName) {
        String testFileName = commandName + FileUtil.TRIM_TEST;
        String testFilePath = FileUtil.PATH_TEST + testFileName;
        ArrayList<String> data = FileUtil.readFileToArrayByFlag(testFilePath, "#");
        LogManager.d("Read data:" + testFilePath + " Done!");
        
        return data;
    }
    
    private static void generationTestData(String commandName) {
        ArrayList<String> templateList = getTempList(commandName);
        HashMap<String, ArrayList<String>> argMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> argList = getArgAndDicMap(commandName, argMap);
        
        for(String arg : argList) {
            
            String argExp = "[" + arg  + "]";
            String argTempExp =  "<" + arg  + ">";
            ArrayList<String> tempList = new ArrayList<String>();
            
            for(String temp : templateList) {
//                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//                Matcher m = p.matcher(temp);
//                dest = m.replaceAll("");
                temp = temp.replaceAll("\r", "");
                //temp.replace("\r", "");
                if(temp.contains(argExp)) {
                    ArrayList<String> argDic = argMap.get(arg);
                    for(String word : argDic) {
                        tempList.add(temp.replace(argExp, word) + " " + arg + ":" + word);
                    }
                } else if(temp.contains(argTempExp)) {
                    ArrayList<String> argDic = argMap.get(arg);
                    for(String word : argDic) {
                        tempList.add(temp.replace(argTempExp, word));
                    }
                } else {
                    tempList.add(temp);
                }
            }
            
            templateList = tempList;
        }

        FileUtil.writeArrayStringToFileByTrim(templateList, FileUtil.PATH_TEST + commandName + FileUtil.TRIM_TEST, false);
    }
    
    
    private static ArrayList<String> getArgAndDicMap(String commandName, 
            HashMap<String, ArrayList<String>> map) {
        if(map == null) {
            LogManager.e("input list is null");
            return null;
        }

        ArrayList<String> argList = new ArrayList<String>();
        String path = FileUtil.PATH_TEST + commandName + FileUtil.TRIM_DIC;
        LogManager.i("read dic from:" + path);
        ArrayList<String> temp = FileUtil.getFileContent(path);
        for (String item : temp) {

            String[] src = item.split(",");
            ArrayList<String> dicList = new ArrayList<String>();

            for (int i = 1; i < src.length; i++) {
                dicList.add(src[i]);
            }

            argList.add(src[0]);
            map.put(src[0], dicList);
        }

        return argList;

    }
    
    private static ArrayList<String> getTempList(String commandName) {

        String path = FileUtil.PATH_TEST + commandName + FileUtil.TRIM_TEMP;
        ArrayList<String> tempList = FileUtil.getFileContent(path);
        
        for(int i = 0;i < tempList.size();i++) {
            tempList.set(i, tempList.get(i) + "\r");
        }
        return tempList;
    }
    
    
    /**
     * @param commandName : CommandXXX
     * @param isNeedTrain : if need train or not.If you modified train file, set it to true.
     * @param context 
     */
    private static TestResult testCRF(String commandName, ResultChecker checker, boolean isNeedTrain, Context context) {
        
        int runIndex = 0;
        CRFUtil.init(context, context.getPackageName());
        if(isNeedTrain) {
            CRFUtil.training(commandName);
        }

        LogManager.d("=========Test data:" + commandName + " Start!");
        
        ArrayList<String> testData = getTestDataList(commandName);
        ArrayList<String> matchList = new ArrayList<String>();
        ArrayList<String> errorList = new ArrayList<String>();
        ArrayList<String> noMatchList = new ArrayList<String>();
        
        String headInfo = "Test data count:" + testData.size();
        LogManager.i(headInfo);
        
        long start = System.currentTimeMillis();
        
        for(String text : testData) {
            LogManager.i("Test Index:" + runIndex);
            HashMap<String, String> paramMap = CRFUtil.extractParams(text, commandName);
            
            
            if(paramMap != null && !paramMap.isEmpty()) {
                String arg0 = paramMap.get("arg0");
                String arg1 = paramMap.get("arg1");
                String arg2 = paramMap.get("arg2");
                String output = text;
                if(arg0 != null && !arg0.equals("")) {
                     output += text + "  " + "arg0:" + arg0;
                }
                
                if(arg1 != null && !arg1.equals("")) {
                    output += text + "  " + "arg1:" + arg1;
                }
                
                if(arg2 != null && !arg2.equals("")) {
                    output += text + "  " + "arg2:" + arg2;
                }

                LogManager.d(output);
                matchList.add(output);
            } else {
                noMatchList.add(text + "\r");
                LogManager.w("No match! src:" + text);
            }
            runIndex++;
        }
        

        long end = System.currentTimeMillis();
        long runTime = end - start;
        
        TestResult result = new TestResult(commandName, runTime, testData.size(), matchList.size());
        
        
        outputTestResult(matchList, commandName, FileUtil.TRIM_MATCH, result);
        outputTestResult(noMatchList, commandName, FileUtil.TRIM_NOMATCH, 
                         new TestResult(commandName, runTime, testData.size(), noMatchList.size()));

        LogManager.i("match count:" + (matchList.size() - 1));
        LogManager.i("nomatch count:" + (noMatchList.size() - 1));
        LogManager.i("run time:" + runTime);
        LogManager.d("=========Test data:" + commandName + " Done!");
        
        return result;
    }
    
    
    private static void outputTestResult(ArrayList<String> results, 
                                         String commandName, 
                                         String fileTrim,
                                         TestResult result) {
        String head = result.toString();
        results.add(0, head);
        if(head != null) {
            String path = FileUtil.PATH_RESULT + commandName + fileTrim;
            
            FileUtil.writeArrayStringToFileByTrim(results, path, false);
        }
    }
    
    /**
     * @param commandName
     * @param runTime
     * @param totalDataCnt
     * @param outputDataCnt
     * @return
     * 
     * ====================================================
     *   Command Name:       commandName
     *   Test Data Count:    totalDataCnt
     *   Matched Data Count: outputDataCnt
     *   Run Time(ms):       runTime
     *   Average Time(ms):   average time
     *   Run Date(YYYY-MM-DD HH:MM:SS): now
     * ====================================================
　　　　*
     */
//    private static String makeOutputHead(String commandName, long runTime, int totalDataCnt, int outputDataCnt) {
//        
//        Date now = new Date();
//        DateFormat dataFormat = DateFormat.getDateTimeInstance();
//        String date = dataFormat.format(now);
//        double averageTime = (double)runTime / totalDataCnt;
//        double matchPer    = 100.0 * (double)outputDataCnt / totalDataCnt;
//        String head = "#====================================================\r\n";
//        head += "  Command Name: " + commandName + "\r\n";
//        head += "  Test Data Count: " + totalDataCnt + "\r\n";
//        head += "  Matched Data Count: " + outputDataCnt + "\r\n";
//        head += "  Matched per(%): " + matchPer + "\r\n";
//        head += "  Run Time(ms): " + runTime + "\r\n";
//        head += "  Average Time(ms): " + averageTime + "\r\n";
//        head += "  Run Date(YYYY/MM/DD HH:MM:SS): " + date + "\r\n";
//        head += "#====================================================\r\n";
//        
//        return head;
//    }
}
