package Domain;

import Acquaintance.IReader;
import Acquaintance.IWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersistanceContact {

    private static PersistanceContact instance = null;
    private IWriter writer;
    private IReader reader;
    private int currentCaseID;
    private int currentCaseRequestID;
    private int currentEmployeeID;

    public static PersistanceContact getInstance() {
        if (instance == null) {
            instance = new PersistanceContact();
        }
        return instance;
    }

    private PersistanceContact() {
    }

    /**
     * Injects a writer to the PersistanceContact.
     *
     * @param writer
     */
    public void injectWriter(IWriter writer) {
        this.writer = writer;
    }

    /**
     * Injects a reader to the PersistanceContact.
     *
     * @param reader
     */
    public void injectReader(IReader reader) {
        this.reader = reader;
        readCurrentIDs();
    }

    /**
     * Saves the case request.
     *
     * @param caseRequest
     * @return
     */
    public int saveCaseRequest(CaseRequest caseRequest) {
        System.out.println("PersistenceContact: saveCaseRequest");
        return writer.writeCaseRequest(caseRequest);
    }

    /**
     * Saves the case
     *
     * @param c
     * @return String for the user.
     */
    public int saveCase(Case c) {
        System.out.println("PersistenceContact: saveCase");
        return writer.writeCase(c);
    }

    /**
     * Save the employee to the database
     *
     * @param employee
     * @return
     */
    public int saveEmployee(Employee employee) {
        System.out.println("PersistenceContact: saveEmployee");
        
        int returnInt = -1;
        if (employee instanceof Secretary) {
            returnInt = writer.writeEmployee(employee, 1);
        } else if (employee instanceof SocialWorker) {
            returnInt = writer.writeEmployee(employee, 2);
        } else if (employee instanceof Admin) {
            returnInt = writer.writeEmployee(employee, 3);
        } else {
            System.out.println("PersistenceContact: Illegal position number in saveEmployee()");
        }

        return returnInt;
    }

    /**
     * Deletes the employee from the database
     *
     * @param id ID of employee
     * @return
     */
    public int deleteEmployee(int id) {
        System.out.println("PersistenceContact: deleteEmployee");
        return writer.deleteEmployee(id);
    }

    public Employee login(String username, String password) {
        System.out.println("PersistenceContact: login");
        
        String[] e = reader.login(username, password);
        if (e[0] == null) {
            System.out.println("User doesn't exist");
            return null;
        }
        Employee employee = createEmployee(e);

        return employee;
    }

    /**
     * Saves a log of the action performed
     *
     * @param employeeID
     * @param action
     * @param desc
     */
    public void logAction(int employeeID, LogAction action, String desc) {
        System.out.println("PersistenceContact: logAction");
        
        Log log = new Log(employeeID, action, desc);
        writer.writeLog(log);
    }

    /**
     * Gets the case request based on the case request ID
     *
     * @param ID the id of the CaseRequest
     * @return CaseRequest
     */
    public CaseRequest getCaseRequest(int ID) {
        String[] cr = reader.getCaseRequest(ID);
        if (cr[0] == null) {
            System.out.println("CaseRequest wasn't found");
            return null;
        }
        
        String CPR = cr[8];                             //CPR
        int employeeID = Integer.parseInt(cr[0]);       //EmployeeID
        int caseRequestID = Integer.parseInt(cr[1]);    //CaseReqID

        Integer citizenPhoneNr = cr[13].trim().equals("-1") ? null : Integer.parseInt(cr[13]);
        Person citizen = new Person(CPR, cr[9], cr[10].charAt(0), cr[11], cr[12], citizenPhoneNr, cr[14]);
        Date dateCreated = new Date(Long.parseLong(cr[15]));
        Date dateModified = new Date(Long.parseLong(cr[16]));

        CaseRequest currentCaseRequest = new CaseRequest(employeeID, caseRequestID, citizen, dateCreated, dateModified);

        currentCaseRequest.setDescription(cr[2]);
        currentCaseRequest.setMessageClear(getBooleanFromInput(cr[3]));
        currentCaseRequest.setCarePackageRequested(cr[4].split("#"));
        currentCaseRequest.setRehousingPackageRequested(cr[5]);
        currentCaseRequest.setRequestPerson(cr[6]);
        currentCaseRequest.setCitizenInformed(getBooleanFromInput(cr[7]));

        return currentCaseRequest;
    }

    /**
     * Gets the case based on the case ID
     *
     * @param ID The id of the Case
     * @return Case
     */
    public Case getCase(int ID) {
        String[] c = reader.getCase(ID);
        if (c[0] == null) {
            System.out.println("Case wasn't found");
            return null;
        }

        int caseRequestID = Integer.parseInt(c[2]);
        int caseID = Integer.parseInt(c[0]);
        CaseRequest caseRequest = getCaseRequest(caseRequestID);
        logAction(DomainContact.getInstance().getCurrentUser().getId(), LogAction.GET_CASE_REQUEST, "Retrieved CaseRequest (ID " + caseRequest + ") for Case (ID " + caseID + ")");
        Date dateCreated = new Date(Long.parseLong(c[15]));
        Date dateModified = new Date(Long.parseLong(c[16]));
        Case currentCase = new Case(caseID, Integer.parseInt(c[1]), caseRequest, dateCreated, dateModified);
        currentCase.setNextAppointment(c[3]);
        currentCase.setGuardianship(c[4]);
        currentCase.setPersonalHelper(c[5]);
        currentCase.setPersonalHelperPowerOfAttorney(c[6]);
        currentCase.setCitizenRights(c[7]);
        currentCase.setCitizenInformedElectronic(getBooleanFromInput(c[8]));
        currentCase.setConsent(getBooleanFromInput(c[9]));
        currentCase.setConsentType(c[10]);
        currentCase.setCollectCitizenInfo(c[11].split("#"));
        currentCase.setSpecialCircumstances(c[12]);
        currentCase.setDifferentCommune(c[13]);
        currentCase.setState(c[14]);
        return currentCase;
    }
    
    public List<CaseObject> getCaseObject(String citizenCPR) {
        List<CaseObject> caseObjects = new ArrayList<>();
        
        for (String[] simpleCase : reader.getSimpleCases(citizenCPR)) {
            int id = Integer.parseInt(simpleCase[0]);
            int employeeID = Integer.parseInt(simpleCase[1]);
            String description = simpleCase[2];
            Date dateCreated = new Date(Long.parseLong(simpleCase[3]));
            
            CaseObject caseObject = new CaseObject(id, employeeID, "Case", description, dateCreated);
            caseObjects.add(caseObject);
        }
        
        for (String[] simpleCase : reader.getSimpleCaseRequests(citizenCPR)) {
            int id = Integer.parseInt(simpleCase[0]);
            int employeeID = Integer.parseInt(simpleCase[1]);
            String description = simpleCase[2];
            Date dateCreated = new Date(Long.parseLong(simpleCase[3]));
            
            CaseObject caseObject = new CaseObject(id, employeeID, "CaseRequest", description, dateCreated);
            caseObjects.add(caseObject);
        }
        
        return caseObjects;
    }

    /**
     * Gets the person based on a CPR number
     *
     * @param CPR
     * @return Person
     */
    public Person getPerson(String CPR) {
        String[] p = reader.getPerson(CPR);
        if (p[0] == null) {
            System.out.println("Person wasnt found");
            return null;
        }

        //Integer personPhoneNr = p[5].equals("") ? null : Integer.parseInt(p[5]);

        Person person = new Person(p[0], p[1], p[2].charAt(0), p[3], p[4], null, "");

        return person;
    }

    /**
     * Gets an employee based on the ID
     *
     * @param ID
     * @return Employee
     */
    public Employee getEmployee(int ID) {
        String[] e = reader.getEmployee(ID);
        if (e[0] == null) {
            System.out.println("Employee wasn't found");
            return null;
        }
        Employee employee = createEmployee(e);

        return employee;
    }

    /**
     * Gets a case request ID for a new case request.
     *
     * @return int currentCaseRequestID
     */
    public int getNewCaseRequestID() {
        currentCaseRequestID++;
        writeCurrentIDs();
        return currentCaseRequestID;
    }

    /**
     * Gets a case ID for a new case.
     *
     * @return int currentCaseID
     */
    public int getNewCaseID() {
        currentCaseID++;
        writeCurrentIDs();
        return currentCaseID;
    }

    /**
     * Gets a employee ID for a new employee.
     *
     * @return int currentEmployeeID
     */
    public int getNewEmployeeID() {
        currentEmployeeID++;
        writeCurrentIDs();
        return currentEmployeeID;
    }

    /**
     * Method is used to set the fields in PersistenceContact. This method is
     * called when a reader is injected to PersistanceContant.
     */
    private void readCurrentIDs() {
        int[] ids = reader.getCurrentIDs();
        this.currentCaseID = ids[0];
        this.currentCaseRequestID = ids[1];
        this.currentEmployeeID = ids[2];
    }

    /**
     * Writes the current IDs to the database. This method is called whenever
     * there is a change in those fields.
     */
    private void writeCurrentIDs() {
        //writer.writeIDs(currentCaseID, currentCaseRequestID, currentEmployeeID);
    }

    private Employee createEmployee(String[] e) {
        Employee employee = null;
        Integer employeePhoneNr = e[5].trim().equals("-1") ? null : Integer.parseInt(e[5]);

        switch (e[10]) {
            case "1":
                employee = new Secretary(e[0].trim(), e[1], e[2].charAt(0), e[3], e[4], employeePhoneNr, e[6], Integer.parseInt(e[7]), e[8], e[9]);
                break;
            case "2":
                employee = new SocialWorker(e[0].trim(), e[1], e[2].charAt(0), e[3], e[4], employeePhoneNr, e[6], Integer.parseInt(e[7]), e[8], e[9]);
                break;
            case "3":
                employee = new Admin(e[0].trim(), e[1], e[2].charAt(0), e[3], e[4], employeePhoneNr, e[6], Integer.parseInt(e[7]), e[8], e[9]);
                break;
            default:
                System.out.println("Wrong position number retrieved.");
                break;
        }
        return employee;
    }
//    public static void main(String[] args) {
//        PersistanceContact pc = PersistanceContact.getInstance();
//        pc.injectReader(new ReadDB());
//        System.out.println(pc.getCaseObject("36217861"));
//        System.out.println(pc.getCase(1));
//        System.out.println(pc.getCaseRequest(1).toString());
//    }
    
    private boolean getBooleanFromInput(String input) {
        if (input.equalsIgnoreCase("T") || input.equalsIgnoreCase("true")) {
            return true;
        } else if (input.equalsIgnoreCase("F") || input.equalsIgnoreCase("false")) {
            return false;
        } else {
            System.out.println("Method getBooleanFromInput: CANNOT CONVERT STRING TO BOOLEAN");
            return false;
        }
    }
}
