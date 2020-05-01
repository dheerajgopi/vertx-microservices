package org.example.microservicecommon.http;

public interface QueryPagination {

    Integer limit();

    Integer offset();

    Integer pageNumber();

}
