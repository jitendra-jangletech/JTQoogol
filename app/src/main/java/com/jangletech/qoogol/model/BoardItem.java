package com.jangletech.qoogol.model;

public class BoardItem {

    public BoardItem(String boardName, String board) {
        this.boardName = boardName;
        this.board = board;
    }

    private String boardName;
    private String board;

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }


}
