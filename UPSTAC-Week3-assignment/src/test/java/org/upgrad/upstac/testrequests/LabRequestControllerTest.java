package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.TestStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Slf4j
class LabRequestControllerTest {


    @Autowired
    LabRequestController labRequestController;




    @Autowired
    TestRequestQueryService testRequestQueryService;


    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);
        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Lab Test using assignForLabTest() method
        // from labRequestController class. Pass the request id of testRequest object.

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_IN_PROGRESS'
        // make use of assertNotNull() method to make sure that the lab result of second object is not null
        // use getLabResult() method to get the lab result
        TestRequest ass_req = labRequestController.assignForLabTest(testRequest.getRequestId());
        assertThat(testRequest.getRequestId(), equalTo(ass_req.getRequestId()));
        assertThat(ass_req.getStatus(), equalTo(RequestStatus.LAB_TEST_IN_PROGRESS));
        assertNotNull(ass_req.getLabResult());

    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -34L;

        //Implement this method


        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForLabTest() method
        // of labRequestController with InvalidRequestId as Id


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        ResponseStatusException except = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.assignForLabTest(InvalidRequestId);
        });

        assertThat(except.getMessage(), containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter

        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'LAB_TEST_IN_PROGRESS'. Make use of updateLabTest() method from labRequestController class (Pass the previously created two objects as parameters)

        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_COMPLETED'
        // 3. the results of both the objects created should be same. Make use of getLabResult() method to get the results.
        CreateLabResult result_from_lab = getCreateLabResult(testRequest);
        TestRequest requestUpdate = labRequestController.updateLabTest(testRequest.getRequestId(), result_from_lab);
        assertThat(requestUpdate.getRequestId(), equalTo(testRequest.getRequestId()));
        assertThat(requestUpdate.getStatus(), equalTo(RequestStatus.LAB_TEST_COMPLETED));
        assertThat(requestUpdate.getLabResult().getResult(), equalTo(result_from_lab.getResult()));


    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);


        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        CreateLabResult labResults = getCreateLabResult(testRequest);

        ResponseStatusException except = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.updateLabTest(-1L, labResults);
        });

        assertThat(except.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Set the result of the above created object to null.

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "ConstraintViolationException"
        CreateLabResult result_from_lab = getCreateLabResult(testRequest);
        /**
         * calling_updateLabTest_with_invalid_empty_status_should_throw_exception
         * Status is set internally and not via controller. I am assuming status here means Result
         */
        result_from_lab.setResult(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            labRequestController.updateLabTest(testRequest.getRequestId(), result_from_lab);
        });

        assertThat(ex.getMessage(), containsString("ConstraintViolationException"));
    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {

        String bloodPressure, heartBeat, temperature, oxygenLevel, comments = "";
        bloodPressure = StringUtils.isEmpty(testRequest.getLabResult().getBloodPressure())? "80": testRequest.getLabResult().getBloodPressure();
        heartBeat = StringUtils.isEmpty(testRequest.getLabResult().getHeartBeat())? "70": testRequest.getLabResult().getHeartBeat();
        temperature = StringUtils.isEmpty(testRequest.getLabResult().getTemperature())? "97": testRequest.getLabResult().getTemperature();
        oxygenLevel = StringUtils.isEmpty(testRequest.getLabResult().getOxygenLevel())? "99": testRequest.getLabResult().getOxygenLevel();
        comments = StringUtils.isEmpty(testRequest.getLabResult().getComments())? "Reports are fine": testRequest.getLabResult().getComments();
        TestStatus status = testRequest.getLabResult().getResult() == null ? TestStatus.NEGATIVE : testRequest.getLabResult().getResult();

        CreateLabResult labResult = new CreateLabResult();
        labResult.setBloodPressure(bloodPressure);
        labResult.setHeartBeat(heartBeat);
        labResult.setTemperature(temperature);
        labResult.setOxygenLevel(oxygenLevel);
        labResult.setComments(comments);
        labResult.setResult(status);

        return labResult; // Replace this line with your code
    }

}