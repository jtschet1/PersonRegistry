package model;

import java.util.ArrayList;
import java.util.List;

public class FetchResults {
    private int totalElements;
    private int totalPages;
    private int pageSize;
    private int offset;
    private int firstIndex;
    private int lastIndex;
    private List<Person> people;

    public FetchResults() {
    }

    public FetchResults(int totalRows, int pageSize, int offset, ArrayList<Person> people, int totalElements) {
        //this.totalRows = totalRows;
        this.pageSize = pageSize;
        this.offset = offset;
        this.people = people;
        this.totalElements = totalElements;
    }



    //accessors


    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
