import cqjsdk.msg.Msg;
import cqjsdk.msg.RecvGroupMsg;
import cqjsdk.msg.SendGroupMsg;
import cqjsdk.server.CQJModule;
import cqjsdk.server.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class WTD extends CQJModule {
    private PreparedStatement is_wtd;
    //private PreparedStatement to_wtd;
    private PreparedStatement new_wt;
    //private PreparedStatement make_emoji;
    private PreparedStatement get_latest;
    private PreparedStatement group_get_latest;
    public WTD(String driver, String url, String username, String password){
        Connection conn = connectToDB(driver, url, username, password);
        try {
            if (conn != null && !conn.isClosed()) {
                String[] strings = {"GroupMessage"};
                register(strings);
                is_wtd = conn.prepareStatement("SELECT * FROM img.record WHERE `group`= ?  AND `md5` = ?  ", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                //to_wtd = conn.prepareStatement("UPDATE ? SET times = times + 1 WHERE md5 = ?");
                new_wt = conn.prepareStatement("INSERT INTO img.record (qq, `group`, times, emoji, md5) VALUES (?, ?, 1, 0, ?,?)");
                //make_emoji = conn.prepareStatement("UPDATE ? SET eomji = 1 WHERE md5 = ?");
                get_latest = conn.prepareStatement("SELECT * FROM img.record WHERE `group` = ? AND `qq` = ?   ORDER BY id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                group_get_latest = conn.prepareStatement("SELECT * FROM img.record WHERE `group` = ?  ORDER BY id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        Logger.Log("WTD模块加载");
    }

    private Connection connectToDB(String driver, String url, String username, String password){
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url,username,password);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> water(String group, String qq, String[] md5s){
        ArrayList<Integer> wtd_times = new ArrayList<Integer>();
        for(String md5: md5s){
            try {
                is_wtd.setString(1, group);
                is_wtd.setString(2, md5);
                ResultSet rs = is_wtd.executeQuery();
                if(!rs.next()){
                    rs.moveToInsertRow();
                    rs.updateNull(1);
                    rs.updateString(2, qq);
                    rs.updateString(3, group);
                    rs.updateInt(4, 1);
                    rs.updateInt(5,0);
                    rs.updateString(6, md5);
                    rs.insertRow();
                    rs.moveToCurrentRow();
                    wtd_times.add(0);
                }
                else{
                    String origin_qq = rs.getString(2);
                    Integer emoji = rs.getInt(5);
                    if(qq.equals(origin_qq)){
                        wtd_times.add(-1);
                    }
                    else if(emoji == 1){
                        wtd_times.add(0);
                    }
                    else{
                        Integer times = rs.getInt(4);
                        rs.updateInt(4,  times + 1);
                        rs.updateRow();
                        wtd_times.add(times);
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();;
            }
        }
        return wtd_times;
    }

    public void tagEmoji(String group,String qq){
        try {
            get_latest.setString(1,group);
            get_latest.setString(2,qq);
            ResultSet rs =get_latest.executeQuery();
            if(rs.next()){
                rs.updateInt(5, 1);
                rs.updateRow();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tagEmoji(String group){
        try {
            group_get_latest.setString(1,group);
            ResultSet rs =group_get_latest.executeQuery();
            if(rs.next()){
                rs.updateInt(5, 1);
                rs.updateRow();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getWtd(Integer times){
        String wtd = "wt";
        for(int i=0;i<times;i++){
            wtd = wtd.concat("d");
        }
        return wtd;
    }

    protected Msg dealGroupMsg(RecvGroupMsg msg){
        String[] md5s = getImages(msg.getText());
        String group = msg.getGroup();
        String qq = msg.getQq();
        if(md5s.length == 0 )
            return Msg.Next(msg);
        ArrayList<Integer> wtd_times = water(group, qq, md5s);
        if(wtd_times.size() > 0) {
            SendGroupMsg smsg = new SendGroupMsg(group);
            if (wtd_times.size() == 1) {
                Integer times = wtd_times.get(0);
                if (times >= 1) {
                    smsg.setText(getWtd(times));
                }
                else return Msg.Next(msg);
            } else {
                String text = "";
                for (Integer times : wtd_times) {
                    if (times >= 1) {
                        String wtd = getWtd(times);
                        text = text.concat(wtd).concat(" ");
                    }
                    else text= text.concat("unwtd ");
                }
                smsg.setText(text);
            }
            return Msg.SendandNext(smsg);
        }
        else return Msg.Next(msg);
    }
}