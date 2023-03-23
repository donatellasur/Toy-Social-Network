package domain;

public class Request extends Entity<Long> {
    private Long idUser1;
    private Long idUser2;
    private String status;

    public Request(Long idUser1, Long idUser2, String status) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = status;
    }

    public Long getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(Long idUser1) {
        this.idUser1 = idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(Long idUser2) {
        this.idUser2 = idUser2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
