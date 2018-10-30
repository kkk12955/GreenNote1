package com.im.tku.greennote;

/**
 * Created by kk on 2017/7/26.
 */

public class Curse
    {
        private String Name ;
        private String Teacher;
        private String Location;
        private int Date;
        private Time Start ;
        private Time End ;
        protected Curse (String Name ,String Teacher, String Location , int Date , Time Start ,Time End){
            this.Name = Name ; this.Teacher = Teacher ; this.Location = Location ;  this.Date = Date ; this.Start = Start ; this.End = End;

        }

        protected int get_Curse_Date(){
            return this.Date;
        }
        protected String get_Curse_Name(){
            return this.Name;
        }
        protected Time get_Curse_EndTime(){
            return this.End;
        }
        protected Time get_Curse_StartTime(){
            return this.Start;
        }
        public String toString(){
            return Name+","+Teacher+","+Location+","+Date+","+Start+"~"+End+"\n";
        }

}
