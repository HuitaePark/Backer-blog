package com.baki.backer.domain.post;

public enum Category {
    QUESTION(1), INFORMATION(2),HUMOR(3);

    private final int value;

    Category(int value) {
        this.value = value;
    }
    public int intValue(){
        return value;
    }
    public static Category valueOf(int value){
        return switch (value){
          case 1 -> QUESTION;
          case 2 -> INFORMATION;
          case 3 -> HUMOR;
          default -> throw new AssertionError("unknown value : " + value);
        };
    }
}
