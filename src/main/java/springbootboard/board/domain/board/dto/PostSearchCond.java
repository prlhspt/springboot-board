package springbootboard.board.domain.board.dto;

import lombok.Data;

@Data
public class PostSearchCond {

    private String title;
    private String content;
    private String writer;

}
