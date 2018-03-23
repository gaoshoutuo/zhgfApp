package com.android.zhgf.zhgf.database.bean;

import com.android.zhgf.zhgf.JavaBean.CurrentJGMB;
import com.android.zhgf.zhgf.JavaBean.JGMB;
import com.android.zhgf.zhgf.JavaBean.News.JGMBList;
import com.android.zhgf.zhgf.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by TZXX001 on 2017/11/21.
 */

public class Contact {

    private String node_id;

    private String name;

    private String telephoneNo;

    private String util;

    public String getUtil() {
        return util;
    }

    public void setUtil(String util) {
        this.util = util;
    }

    private String parent_id;

    private int personFlg;

    private boolean onlineBln;

    public boolean isOnlineBln() {
        return onlineBln;
    }

    public void setOnlineBln(boolean onlineBln) {
        this.onlineBln = onlineBln;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public int getPersonFlg() {
        return personFlg;
    }

    public void setPersonFlg(int personFlg) {
        this.personFlg = personFlg;
    }

    public static <T> List<T> jsonToList(String json, Class<T[]> clazz)
    {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    public ArrayList<Contact> getcurrentJGMBList(String jsonString){
        ArrayList<Contact> contacts =  new ArrayList<Contact>();



       /* JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray();*/

        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<CurrentJGMB> jgmbList = new ArrayList<>();

        //加强for循环遍历JsonArray
        for (JsonElement jgmbtemp : jsonArray) {
            //使用GSON，直接转成Bean对象
            CurrentJGMB jgmb = gson.fromJson(jgmbtemp, CurrentJGMB.class);
            jgmbList.add(jgmb);
        }

        int parentId = 999;
        for(int i = 0;i < jgmbList.size();i++){
            CurrentJGMB jgmb = jgmbList.get(i);
            Contact contact = new Contact();
            contact.setNode_id(String.valueOf(i));
            contact.setName(jgmb.getXm());
            contact.setTelephoneNo(jgmb.getBrdh());
            contact.setUtil(jgmb.getJzdm3() + " " + jgmb.getMbzw() + " "+ jgmb.getMblb());
            contact.setPersonFlg(1);
            contact.setParent_id(String.valueOf(parentId));
            contacts.add(contact);
        }
        return contacts;
    }

    public ArrayList<Contact> getJGMBList(String jsonString){

        ArrayList<Contact> contacts =  new ArrayList<Contact>();



        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();

        Gson gson = new Gson();
        ArrayList<JGMB> jgmbList = new ArrayList<>();

        //加强for循环遍历JsonArray
        for (JsonElement jgmbtemp : jsonArray) {
            //使用GSON，直接转成Bean对象
            JGMB jgmb = gson.fromJson(jgmbtemp, JGMB.class);
            jgmbList.add(jgmb);
        }

        int parentId = 0;
        int childparentId = 0;
        for(int i = 0;i < jgmbList.size();i++){
            JGMB jgmb = jgmbList.get(i);
            Contact contact = new Contact();
            if(jgmb.getCreateUserName() == null){
                contact.setNode_id(String.valueOf(i));
                contact.setName(jgmb.getFullName());
                contact.setPersonFlg(0);
                contact.setParent_id(String.valueOf(childparentId));
            }else {
                if(jgmb.getCreateUserName().equals("超级管理员")){

                    if(parentId == 0){
                        contact.setParent_id(String.valueOf(parentId));
                    }else{
                        contact.setParent_id(String.valueOf(parentId + 1));
                    }
                    childparentId = i;
                    contact.setNode_id(String.valueOf(i));
                    contact.setName(jgmb.getFullName());
                    contact.setPersonFlg(0);


                }
            }
            /*if(jgmb.getCreateUserName().equals("超级管理员")){
                parentId++;
                contact.setNode_id(String.valueOf(i));
                contact.setName(jgmb.getFullName());
                contact.setPersonFlg(0);
                contact.setParent_id(String.valueOf(parentId));

            }else{

                contact.setNode_id(String.valueOf(i));
                contact.setName(jgmb.getFullName());
                contact.setPersonFlg(0);
                contact.setParent_id(String.valueOf(parentId));

            }*/
            contacts.add(contact);
        }

        return contacts;

        /*JGMBList jgmbList = new Gson().fromJson(jsonString,JGMBList.class);

        if (jgmbList == null){
            return null;
        }
        List<JGMB> jgmbs = jgmbList.getJgmb();

        int parentId = 0;
        for(int i = 0;i < jgmbs.size();){
            JGMB jgmb = jgmbs.get(i);
            Contact contact = new Contact();
            if(jgmb.getCreateUserName().equals("超级管理员")){
                parentId++;
                contact.setNode_id(String.valueOf(i));
                contact.setName(jgmb.getFullName());
                contact.setPersonFlg(0);
                contact.setParent_id(String.valueOf(parentId));

            }else{

                contact.setNode_id(String.valueOf(i));
                contact.setName(jgmb.getFullName());
                contact.setPersonFlg(0);
                contact.setParent_id(String.valueOf(parentId));

            }
            contacts.add(contact);
        }*/


    }

}
