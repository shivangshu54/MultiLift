package controller;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.commons.RequestedDirection;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.service.LiftAssignerA;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;


public class LiftAssignerTest {

    @InjectMocks
    LiftAssignerA liftAssigner;

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
        liftAssigner.assignInternalRequests(lift, 2);
        int valueUp = 5;
        int valueDown = 3;
        for (Iterator i = lift.getFloorRequestsGoingUp().iterator(); i.hasNext(); ) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueUp++);
        }
        for (Iterator i = lift.getFlooRequestsGoingDown().iterator(); i.hasNext(); ) {
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
        liftAssigner.assignInternalRequests(lift, 2);
        int valueUp = 5;
        int valueDown = 3;
        for (Iterator i = lift.getFloorRequestsGoingUp().iterator(); i.hasNext(); ) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueUp++);
        }
        for (Iterator i = lift.getFlooRequestsGoingDown().iterator(); i.hasNext(); ) {
            Integer integer = (Integer) i.next();
            Assert.assertTrue(integer == valueDown--);
        }
    }

    @Test
    public void testAssignExternalRequest() {
        LiftStore liftStore = LiftStore.getInstance();
        Lift l1 = new Lift(1, 0);
        l1.setStatus(LiftStatus.IDLE);
        liftStore.addLiftsToStore(l1);
        Lift l2 = new Lift(2, 4);
        l2.setStatus(LiftStatus.MOVING_DOWN);
        liftStore.addLiftsToStore(l2);
        Lift l3 = new Lift(3, 6);
        l3.setStatus(LiftStatus.MOVING_DOWN);
        liftStore.addLiftsToStore(l3);
        Lift l4 = new Lift(4, 2);
        l3.setStatus(LiftStatus.MOVING_DOWN);
        liftStore.addLiftsToStore(l4);
        Lift l5 = new Lift(5, 2);
        l3.setStatus(LiftStatus.MOVING_UP);
        liftStore.addLiftsToStore(l5);
        ExternalRequest request = new ExternalRequest();
        request.setFromFloor(3);
        request.setRequestedDirection(RequestedDirection.DOWN);
        Lift liftAssigned = liftAssigner.assignLift(request);
        Assert.assertEquals(l2.getId(),liftAssigned.getId(),"Wrong lift Assigned");
    }

}
