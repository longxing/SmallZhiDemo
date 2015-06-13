package com.iii360.box.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTagData {

    public static String[] sTagQuestion = { "哪种性格说的是你?", "你最喜欢的运动是?", "你爱读什么书?", "什么音乐最让你痴迷?", "最近爱看什么剧?", "你现在的状态是?", "360行，你在哪一行?", "你是谁的粉?",
            "你是哪种游戏控?", "说说你的星座年代?", "你来自那一派?", "什么音乐最让你痴迷 ?", "你是哪种动漫控?", "你是什么电影控?", "你是谁的运动粉?" };

    public static String[][] sTagItem = {
            { "幽默", "乐观", "低调", "完美主义", "三分钟热度", "善良", "阳光", "直率", "执着", "体贴", "八卦", "内敛", "温柔", "粗枝大叶", "张扬", "自信", "萌", "纠结", "梦幻", "纯真" },
            { "足球", "篮球", "乒乓球", "网球", "台球", "羽毛球", "游泳", "瑜伽", "跆拳道", "钓鱼", "象棋", "骑行", "爬山", "跑步", "太极", "高尔夫", "拉丁舞", "卡丁车" },
            { "倒带人生", "诗经", "简爱", "盗墓笔记 ", "明朝那些事 ", "格林童话 ", "活着", "天龙八部 ", "三国演义 ", "哈利波特", "围城", "百年孤独 ", "三体", "悉达多", "小王子", "平凡的世界", "月亮和六便士" },
            { "中国摇滚", "jazz", "民谣", "重金属", "民乐", "新世纪", "朋克", "纯音乐", "独立音乐", "英伦摇滚", "钢琴", "二胡", "古筝", "RAP", "电影原声", "古典音乐", "歌剧", "京剧", "越剧" },
            { "生活大爆炸", "万凰之王", "杨门女将", "美人心计", "倾世皇妃", "宫锁心玉", "迷失", "憨豆先生", "斯巴达克斯", "犯罪心理", "实习生格蕾", "危机边缘", "罗马", "欲望都市", "兄弟", "连双城" },
            { "单身待解救", "静待缘分", " 努力加班", " 心如止水", " 奋斗ing", "幸福ing", "成长ing", "学习ing", "缺爱ing", "减肥Ing", "失恋ing", "热恋ing", "纠结ing", "寂寞ing", "供房ing" },
            { "学生", "公务员", "媒体人", "音乐人", "自由职业者", "白领", "IT民工", "老师", "销售", "餐饮", "医疗", "房地产", "法律", "外出务工", "农业", "金融", "上班族", "创业" },
            { "Johnnydepp", "Bradpitt", "莱昂纳多", "姜文", "梁朝伟", "尼古拉斯凯奇", "汤姆汉克斯", "罗伯特德尼罗", "北野武", "周星驰", "马里奥毛瑞尔", "吴彦祖", "梁家辉", "葛优", "陈柏霖", "陈道明",
                    "木村拓哉" },
            { "穿越火线", "战地之王", "英雄岛", "NBA2K", "生化战场", "LOL", "火力突击" },
            { "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "射手座", "天蝎座", "摩羯座", "水瓶座", "双鱼座", "70后", "80后", "85后", "90后", "00后" },
            { "知道分子", "靠谱女青年", "待解救女青年", "待解救男青年", "外貌协会", "文艺青年", "普通青年", "拉风老年", "技术宅", "购物狂", "乐活族", "月光族", "相信缘分", "小清新" },
            { "电台情歌", "月亮代表我的心", "没那么简单", "最熟悉的陌生人", "荷塘月色", "Nobody", "kissgoodbye", "closetoyou", "爱我别走", "黑暗之光", "旅行的意义", "海阔天空", "光辉岁月", "春天里",
                    "夜夜夜夜" },
            { "火影忍者", "海贼王", "死神", "海绵宝宝", "奥特曼", "哆啦A梦", "猫和老鼠", "蜡笔小新", "柯南", "城市猎人", "变形金刚", "倒霉熊", "死亡笔记", "圣斗士", "葫芦娃", "蓝精灵", "EVA", "天空之城" },
            { "怦然心动", "少年派的奇幻漂流", "城南旧事", "钢的琴", "霸王别姬", "失恋33天", "暮光之城", "死神来了", "老男孩", "我是传奇", "生化危机", "速度与激情", "剪刀手爱德华", "志明与春娇", "她比烟花寂寞", "罗马假日" },
            { "梅西", "李娜", "皇马", "巴萨", "丁俊晖", "潘晓婷", "纳达尔", "林丹", "菲尔普斯", "泰森", "刘翔", "姚明", "王楠", "舒马赫", "乔丹", "科比", "卡卡", "艾佛森", "奥沙利文" } };


    public static Map<String, List<String>> map = new HashMap<String, List<String>>();
    public static List<String> list;

    public static Map<String, List<String>> getMapList() {
        map.clear();

        for (int i = 0; i < sTagQuestion.length; i++) {

            list = new ArrayList<String>();

            for (int j = 0; j < sTagItem[i].length; j++) {
                list.add(sTagItem[i][j].trim());
            }
            map.put(sTagQuestion[i], list);
        }
        return map;
    }
}