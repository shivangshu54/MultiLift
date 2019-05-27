package controller;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.service.LiftAssigner;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;


public class LiftAssignerTest {

    @InjectMocks
    LiftAssigner liftAssigner;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testAssignInternalRequestLiftGoingUp() {
        Lift lift = new Lift(1, 4);
        lift.setStatus(LiftStatus.MOVING_UP);
        liftAssigner.assignInternalRequests(lift, 5);
        liftAssigner.assignInternalRequests(lift, 6);
        liftAssigner.assignInternalRequests(lift, 3);
        liftAssigner.assignInternalRequests(lift,2);
        int valueUp = 5;
        int valueDown = 3;
        for (Iterator i = lift.getFloorRequestsGoingUp().iterator(); i.hasNext();) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueUp++);
        }
        for (Iterator i = lift.getFlooRequestsGoingDown().iterator(); i.hasNext();) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueDown--);
        }
    }

    @Test
    public void testAssignInternalRequestLiftGoingDown() {
        Lift lift = new Lift(2, 4);
        lift.setStatus(LiftStatus.MOVING_DOWN);
        liftAssigner.assignInternalRequests(lift, 5);
        liftAssigner.assignInternalRequests(lift, 6);
        liftAssigner.assignInternalRequests(lift, 3);
        liftAssigner.assignInternalRequests(lift,2);
        int valueUp = 5;
        int valueDown = 3;
        for (Iterator i = lift.getFloorRequestsGoingUp().iterator(); i.hasNext();) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueUp++);
        }
        for (Iterator i = lift.getFlooRequestsGoingDown().iterator(); i.hasNext();) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueDown--);
        }
    }

    @Test
    public void testAssignExternalRequest() {

    }

}
