package springbootboard.board.web.dto;

import lombok.Data;

@Data
public class SearchRequestDto {
    private String searchType;
    private String keyword;
}
