package com.brainsporthero.cp.pythagoras;

public class PythagreanLoops {
    public static void main(String[] args)  {

        int a, b, c;
        for(a = 1; a <= 10 ; a++) {
            for(b = 1; b <= 10; b++) {
                for(c = 1; c <= 100; c++) {
                    if((Math.pow(a, 2) + Math.pow(b, 2)) == Math.pow(c, 2)) {
                            System.out.println(a + " " + b + " " + c);
                    }
                }
            }
        }
    }
}