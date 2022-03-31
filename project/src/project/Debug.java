package project;

import uml.*;

public class Debug{
    //run by command $ant dbg;
    //use for debug without GUI;
    public static void main(String[] args) {
        ClassDiagram  cd = new ClassDiagram("diag1");
        System.out.print(cd.getName());
    }
}