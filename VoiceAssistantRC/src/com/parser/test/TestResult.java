package com.parser.test;

import java.text.DateFormat;
import java.util.Date;

public class TestResult {
    public String _commandName;
    public int _totalDataCnt;
    public int _outputDataCnt;
    public long _runTime;
    public double _averageTime;
    public double _matchPer;
    public String _date;
    public TestResult(String commandName, long runTime, int totalDataCnt, int outputDataCnt) {
        _commandName = commandName;
        _runTime = runTime;
        _totalDataCnt = totalDataCnt;
        _outputDataCnt = outputDataCnt;
        _averageTime = (double)runTime / totalDataCnt;
        _matchPer    = 100.0 * (double)outputDataCnt / totalDataCnt;
        Date now = new Date();
        DateFormat dataFormat = DateFormat.getDateTimeInstance();
        _date = dataFormat.format(now);
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
    @Override
    public String toString() {
        String head = "#====================================================\r\n";
        head += "  Command Name: " + _commandName + "\r\n";
        head += "  Test Data Count: " + _totalDataCnt + "\r\n";
        head += "  Matched Data Count: " + _outputDataCnt + "\r\n";
        head += "  Matched per(%): " + _matchPer + "\r\n";
        head += "  Run Time(ms): " + _runTime + "\r\n";
        head += "  Average Time(ms): " + _averageTime + "\r\n";
        head += "  Run Date(YYYY/MM/DD HH:MM:SS): " + _date + "\r\n";
        head += "#====================================================\r\n";
        
        return head;
    }
    
    
}
