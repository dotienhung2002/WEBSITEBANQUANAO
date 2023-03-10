package com.application.fusamate.utils;

public class BaseHelper {
   public String convertRole (int role){
         if (role==1){
             return "admin";
         }
         else{
             return "employee";
         }
    }
}
