package sk.adresa.eaukcia.core.query;

public class Paging {
    private int rowsPerPage, actualPage, totalRows, totalPages;

    public Paging(int rowsPerPage, int actualPage) {
        assert rowsPerPage > 0;
        assert actualPage >= 0;
        this.rowsPerPage = rowsPerPage;
        this.actualPage = actualPage;
        totalPages = 1;
    }


    public Paging(int rowsPerPage, int actualPage, int totalRows) {
        assert rowsPerPage > 0;
        assert actualPage >= 0 ; 
        assert totalRows >= 0;
        this.rowsPerPage = rowsPerPage;
        this.actualPage = actualPage;
        this.totalRows = totalRows;
        totalPages = (totalRows - 1) / rowsPerPage + 1;
        if (actualPage + 1 > totalPages) {
            actualPage = totalPages - 1;
        }
    }
    
    public Paging() {
        this.rowsPerPage = Integer.MAX_VALUE;
        totalPages = 1;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public int getActualPage() {
        return actualPage;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getOffset(){
        return actualPage * rowsPerPage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nObjekt Paging:{");
        sb.append("\n  rowsPerPage:").append(rowsPerPage);
        sb.append("\n  actualPage:").append(actualPage);
        sb.append("\n  totalRows:").append(totalRows);
        sb.append("\n  totalPages:").append(totalPages);
        sb.append("\n}");
        return sb.toString();
    }

}
