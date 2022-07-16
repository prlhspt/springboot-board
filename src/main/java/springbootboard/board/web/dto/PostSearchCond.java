package springbootboard.board.web.dto;

import lombok.Data;

@Data
public class PostSearchCond {

    private String title;
    private String content;
    private String writer;

}
