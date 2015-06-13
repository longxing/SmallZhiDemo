package com.voice.common.util.nlp;

import java.util.HashMap;
import java.util.List;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.voice.assistant.main.KeyList;

/**
 * 用于家电文本语义解析
 * 
 * @author Peter
 * @data 2015年4月10日下午2:10:51
 */
public class HouseCommandProcess {

	/********************************************************************************************
	 * ************ Members Variables
	 *********************************************************************************************/
	private static final String Tag = "HouseCommandProcess";
	private static HouseCommandProcess mLocalProcess = null;
	private HouseCommandParse mHouseCommandParse = null;
	private BasicServiceUnion mUnion = null;
	private BaseContext mBaseContext = null;
	private static HashMap<String, String> localCommands = new HashMap<String, String>();
	static {
		localCommands.put("唱首歌", "");
		localCommands.put("播放歌曲", "");
		localCommands.put("来首歌", "");
		localCommands.put("唱歌", "");
		localCommands.put("播放歌", "");
		localCommands.put("放歌", "");
		localCommands.put("下一曲", "");
		localCommands.put("暂停", "");
		localCommands.put("继续", "");
		localCommands.put("上一首", "");
		localCommands.put("来一首歌", "");
		localCommands.put("唱一首歌", "");
		localCommands.put("来首歌", "");
		localCommands.put("来首音乐", "");
		localCommands.put("播放音乐", "");
		localCommands.put("播放歌曲", "");
		localCommands.put("播放歌曲", "");
		localCommands.put("下一首", "");
		localCommands.put("上一首", "");
		localCommands.put("换一首", "");
		localCommands.put("很好听", "");
		localCommands.put("很难听", "");
		localCommands.put("不好听", "");
		localCommands.put("循环播放", "");
		localCommands.put("我喜欢", "");
		localCommands.put("我讨厌", "");
		localCommands.put("关闭", "");
		localCommands.put("闭上", "");
		localCommands.put("闭掉", "");
		localCommands.put("停止", "");
		localCommands.put("停掉", "");
		localCommands.put("关闭音乐", "");
		localCommands.put("暂停音乐", "");
		localCommands.put("关闭音乐", "");
		localCommands.put("闭上音乐", "");
		localCommands.put("闭掉音乐", "");
		localCommands.put("停止音乐", "");
		localCommands.put("停掉音乐", "");
		localCommands.put("停止唱歌", "");
		localCommands.put("停掉唱歌", "");
		localCommands.put("好听", "");
		localCommands.put("难听", "");
		localCommands.put("喜欢", "");
		localCommands.put("讨厌", "");

	}

	public interface onAnswerOperite {
		public void setOnAnswerOperite(AnswerOperite answerOperite);
	}

	public static HouseCommandProcess getInstace(BasicServiceUnion union) {
		if (mLocalProcess == null) {
			mLocalProcess = new HouseCommandProcess();
			mLocalProcess.mUnion = union;
			mLocalProcess.mBaseContext = union.getBaseContext();
		}
		return mLocalProcess;
	}

	public static void destory() {
		mLocalProcess = null;
	}

	public HouseCommandProcess() {
		init();
	}

	private void init() {
		mHouseCommandParse = HouseCommandParse.getInstanse();
	}

	public boolean handText(final String handText) {
		long tempTime = System.currentTimeMillis();
		LogManager.d(Tag, "========>> handText start compare homeAppliances semanteme on time:" + tempTime);
		// 排除-特别的聊天模式
		if (handText.matches("(.*)(聊天模式)(.*)")) {
			return false;
		}
		boolean flag = getRunExcute(handText, new onAnswerOperite() {
			@Override
			public void setOnAnswerOperite(AnswerOperite answerOperite) {
				if (answerOperite != null) {
					// 支持TTS Debug播报
					if (KeyList.IS_TTS_DEBUG) {
						mUnion.getTTSController().syncPlay("正在使用离线语义识别");
						mUnion.getTTSController().syncPlay("识别结果为家电命令");
					}
					answerOperite.excute();
				}
			}
		});
		LogManager.d(Tag, "========>> handText finish compare homeAppliances semanteme useTime:" + (System.currentTimeMillis() - tempTime) + "ms");
		return flag;
	}

	public boolean getRunExcute(String processText, final onAnswerOperite mOperite) {

		final ParseResultList mList = mHouseCommandParse.HandText(processText);
		// 无结果，或无匹配家电，或相似度太低
		// 当type出现一次diff，则认为不匹配
		if (mList != null && mList.getResultList().size() > 0 && (mList.getScore() < WordSqueeCompare.DIFF)) {
			LogManager.d(Tag, "=====>homeAppliances  grammer compare current list size:" + mList.getResultList().size());
			// 匹配到可执行的家电 执行解析到的所有命令
			if (mList.getResultList().size() == 1) {
				SentenceObject obj = mList.getResultList().get(0);
				AnswerOperite a = new AnswerOperite(obj, mUnion);
				a.setNeedTTS(true);// 需要播报
				mOperite.setOnAnswerOperite(a);
			} else {
				// 多项匹配时，只执行"打开"操作，
				List<SentenceObject> resList = mList.getResultList();
				boolean isOpenCommand = false;
				for (int i = 0; i < resList.size(); i++) {
					SentenceObject obj = resList.get(i);
					if (obj.toString().contains("打开") || obj.toString().contains("开")) {
						isOpenCommand = true;
						AnswerOperite a = new AnswerOperite(obj, mUnion);
						a.setNeedTTS(true);// 需要播报
						mOperite.setOnAnswerOperite(a);
					} else if (!isOpenCommand && (obj.toString().contains("关闭") || obj.toString().contains("关"))) {
						if(resList.size()>=2 && (resList.get(1).toString().contains("打开")||resList.get(1).toString().contains("开"))){
							//回家模式，会匹配到两条命令，（1）如果第一次执行关闭逻辑 不执行 （2）只执行打开命令
							continue;
						}else {
							AnswerOperite a = new AnswerOperite(obj, mUnion);
							a.setNeedTTS(true);// 需要播报
							mOperite.setOnAnswerOperite(a);
						}
						
					} else {
						mUnion.getMainThreadUtil().sendNormalWidget("");
					}
				}
			}

			return true;
		} else {
			// 未匹配到家电
			LogManager.d(Tag, "=====>homeAppliances  grammer not compare current list:" + mList == null ? "null" : "not null");
			if (mList != null) {
				LogManager.d(Tag, "=====>homeAppliances  grammer not compare current list size:" + (mList.getResultList().size() == 0 ? "0" : "not 0"));
				if (mList.getResultList().size() > 0) {
					LogManager.d(Tag, "=====>homeAppliances  grammer not compare current list get score:" + mList.getScore());
				}
			}
			mOperite.setOnAnswerOperite(null);
			String houseCommand = mHouseCommandParse.isHouseCommand(processText);
			if (houseCommand != null) {
				// 未配置家电 支持TTS Debug播报
				if (KeyList.IS_TTS_DEBUG) {
					mUnion.getTTSController().syncPlay("正在使用离线语义识别");
					mUnion.getTTSController().syncPlay("识别结果为家电命令");
				}
				mUnion.getMainThreadUtil().sendNormalWidget("很抱歉，您还没有配置" + houseCommand);
				return true;
			}
			// 离线状态判断
			if (!mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE) && !localCommands.containsKey(processText.trim())) {
				// 支持TTS Debug播报
				if (KeyList.IS_TTS_DEBUG) {
					mUnion.getTTSController().syncPlay("正在使用离线语义识别");
					mUnion.getTTSController().syncPlay("识别结果为家电命令");
				}
				mUnion.getMainThreadUtil().sendNormalWidget("很抱歉，离线状态下，不支持此功能");
				return true;
			}
			return false;
		}
	}

}
