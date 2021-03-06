
package mypackage;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ZL_InformationServiceHttpPost", targetNamespace = "http://tempuri.org/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ZLInformationServiceHttpPost {


    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetOracleSysDate")
    @WebResult(name = "Body", partName = "Body")
    public String getOracleSysDate();

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "Public")
    public void _public(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "OutPatient")
    public void outPatient(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "Hospitalization")
    public void hospitalization(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "MedicalTechnology")
    public void medicalTechnology(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "PhysicalExamination")
    public void physicalExamination(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "UserManager")
    public void userManager(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "Information")
    public void information(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "Custom")
    public void custom(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "InsideBasic")
    public void insideBasic(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

    /**
     * 
     * @param reData
     */
    @WebMethod(operationName = "Inside")
    public void inside(
        @WebParam(name = "ReData", partName = "ReData")
        String reData);

}
