package teammates.common.datatransfer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import java.util.List;

import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

public class statistics {
    
    private int numAlumnos;
    private int numInstructores;
    private int numCursos;
    private int numEvaluaciones;
    
    protected static final PersistenceManager pm = JDOHelper
                                    .getPersistenceManagerFactory("transactions-optional")
                                    .getPersistenceManager();
    
    public statistics(){
        this.numAlumnos=0;
        this.numCursos=0;
        this.numEvaluaciones=0;
        this.numInstructores=0;        
    }     
    
    public int getNumInstructor()
    {
        /*Query q = pm.newQuery(Instructor.class);
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute();
        this.numInstructores=instructorList.size();*/
        int temp=numInstructores;
        return temp;
    }
    
    public int getNumCursos()
    {
        //Query q = pm.newQuery(Course.class);
        
        //@SuppressWarnings("unchecked")
        //List<Course> courseList = (List<Course>) q.execute();
        //numCursos=courseList.size();
       return numCursos;
    }
    
    public int getNumAlumnos()
    {
        /*Query q = pm.newQuery(Student.class);
        
        @SuppressWarnings("unchecked")
        List<Student> studentList = (List<Student>) q.execute();
        this.numAlumnos=studentList.size();*/
        return this.numAlumnos;
    }
    
    public int getNumEvaluaciones()
    {
        return this.numEvaluaciones;
    }
    
}
