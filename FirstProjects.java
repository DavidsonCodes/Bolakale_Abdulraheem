package Week4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;


public class FirstProjects implements Developers{

   private String name;
   private int age;
    private String location;
    private String skill;

    Connection connection = null;
    private void createDevelopersTable() throws SQLException {
        Connection connection1 = connect();
        PreparedStatement statement = connection1.prepareStatement("CREATE TABLE IF NOT EXISTS developers(name Text, age Integer, location Text, skill Text)");
        statement.execute();
        statement.close();
        closeDatabase();
    }


    public void readData(String project){

        try(BufferedReader reader = new BufferedReader(new FileReader(project))){
            String line;

            while( (line = reader.readLine()) != null ){
                String mode = line.substring(0, line.lastIndexOf('#'));
                String[] parts = mode.split(",");
                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                String location = parts[2].trim();
                String skill = parts[3].trim();
                uploadToDatabase(name, age, location, skill);
            }

        }catch (IOException exception){
            System.out.println(exception.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

        private void uploadToDatabase(String name, int age, String location, String skill) throws SQLException{

            try(Connection conn = connect();
            PreparedStatement statement = conn.prepareStatement("INSERT INTO developers VALUES(?, ?, ?, ?)")){

                statement.setString(1, name);
                statement.setInt(2, age);
                statement.setString(3, location);
                statement.setString(4,skill);

                statement.executeUpdate();

            }catch (SQLException e){
                System.out.println(e);
            }
        }

        public void returnData(){

        try(Connection conn = connect();
            Statement statements = conn.createStatement()){

            boolean hasResultSet = statements.execute("select * from developers");

            if(hasResultSet){

                ResultSet resultSet = statements.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();

                int columnCount = metaData.getColumnCount();

                for(int i=1; i<=columnCount; i++){

                    System.out.print(metaData.getColumnLabel(i) + "\t\t\t");
                }
                System.out.println();

                while (resultSet.next()){

                    System.out.printf("%-20s%-10d%-20s%-20s%n", resultSet.getString("name"), resultSet.getInt("age"), resultSet.getString("location"), resultSet.getString("skill"));
                }

            }

        }catch (SQLException e){
            System.out.println(e);
        }




        }
    private Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection("jdbc:mysql://localhost:3306/developer", "root", "Bolanle@291986");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }

    private void closeDatabase() throws SQLException{
        try{
            if( connection != null ){
                connection.close();
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
    }



    @Override
    public ResultSet loadDevelopers() {
        ResultSet resultSet = null;
        try{
            createDevelopersTable();
            readData("C:\\Users\\G\\Documents\\INGRYD\\src\\Week4\\project.txt");
            Connection conn = connect();
            Statement statement = conn.createStatement();
            String selectStatement = "SELECT * FROM developers";
            resultSet = statement.executeQuery(selectStatement);


        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return resultSet;
    }



    }


class ProjectDemo{

    public static void main(String[] args) {
        FirstProjects proj = new FirstProjects();

        try {
            proj.loadDevelopers();
            proj.returnData();
        }catch (Exception e){
            System.out.println(e);
        }


        }
    }


