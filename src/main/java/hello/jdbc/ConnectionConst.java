package hello.jdbc;

//상수에 대한 class이기 때문에 추상클래스로 정의하여 인스턴스를 못만들도록 함
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/db1-jdbc";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
