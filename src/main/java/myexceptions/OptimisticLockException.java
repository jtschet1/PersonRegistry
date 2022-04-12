package myexceptions;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(Exception e) {
        super(e);
    }

    public OptimisticLockException(String msg) {
        super(msg);
    }
}
