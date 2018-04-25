/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author ander
 */
public class ReadTXT implements IReader{
    private File cprFile = new File("CPRRegister.txt");
    private File employeeFile = new File("Employees.txt");
    private File caseFile = new File("Cases.txt");
    private File caseRequestFile = new File("CaseRequests.txt");
    /**
     *
     * @param cpr
     * @return
     */
    @Override
    public String[] getPerson(long cpr) {
        String[] tokens = new String[5]; 
        String[] patient = new String[5];
        String word;
        try (Scanner input = new Scanner(cprFile)){
            while (input.hasNextLine()) {
                word = input.nextLine();
                tokens = word.split(";");
                if (Long.parseLong(tokens[0]) == cpr) {
                    patient = word.split(";");
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND EXCEPTION");
        }
        catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION");
        }
        return patient; 
    }
    
    @Override
    public String[] getEmployee(int id) {
        String[] tokens = new String[5]; 
        String[] employee = new String[10];
        String word;
        try (Scanner input = new Scanner(employeeFile)){
            while (input.hasNextLine()) {
                word = input.nextLine();
                tokens = word.split(";");
                if (Integer.parseInt(tokens[7]) == id) {
                    employee = word.split(";");
                }     
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND EXCEPTION");
        }
        catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION");
        }
        return employee; 
    }
    
    @Override
    public String[] getCase(int id) {
        String[] tokens = new String[16]; 
        String[] cases = new String[16];
        String word;
        try (Scanner input = new Scanner(caseFile)){
            while (input.hasNextLine()) {
                word = input.nextLine();
                tokens = word.split(";");
                if (Integer.parseInt(tokens[0]) == id) {
                    cases = word.split(";");
                }     
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND EXCEPTION");
        }
        catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION");
        }
        return cases; 
    }
    
    
    @Override
    public String[] getCaseRequest(int id) {
        String[] tokens = new String[13]; 
        String[] employee = new String[13];
        String word;
        try (Scanner input = new Scanner(caseFile)){
            while (input.hasNextLine()) {
                word = input.nextLine();
                tokens = word.split(";");
                if (Integer.parseInt(tokens[0]) == id) {
                    employee = word.split(";");
                }     
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND EXCEPTION");
        }
        catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION");
        }
        return employee; 
    }
    
}
