package cqjsdk.server;

import cqjsdk.msg.*;

import java.util.ArrayList;
import java.util.HashMap;

final public class GroupManager extends  CQJModule{
    private static GroupManager groupmgr = new GroupManager();
    private static HashMap<String, ArrayList<CQJModule>> gmap;
    private static CQJModule defaultmodule;
    private GroupManager(){
        gmap = new HashMap<String, ArrayList<CQJModule>>();
        defaultmodule = null;
        String[] strings ={"GroupMessage"};
        register(strings);
    }

    public static GroupManager getGroupmgr() {
        return groupmgr;
    }

    public void setDefaultmodule(CQJModule module){
        defaultmodule = module;
    }

    public void register(String group, CQJModule module){

    }

    protected Boolean dealGroupMsg(RecvGroupMsg msg){
        String group = msg.getGroup();
        if(gmap.containsKey(group){
            gmap.get(group).
        }
        return true;
    }
}
