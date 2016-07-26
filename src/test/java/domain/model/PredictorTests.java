package domain.model;

import domain.Participant;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import predictor.domain.Predictor;

import java.security.Security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The predictor tests
 *
 * @author Claudio E. de Oliveira on on 25/07/16.
 */
public class PredictorTests {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private final Participant owner =
      Participant.builder().id("john").email("john@john").nickname("john").build();

  private final Participant participantTwo =
      Participant.builder().id("mary").email("mary@mary").nickname("mary").build();

  private final Participant participantThree =
      Participant.builder().id("nick").email("nick@nick").nickname("nick").build();

  @Test
  public void testAddInvitation() {
    final Predictor newPredictor = Predictor.createPredictor("10", "10", this.owner, Boolean.TRUE);
    newPredictor.newInvitation(this.participantTwo.getId());
    assertTrue(newPredictor.getInvitations().size() == 1);
  }

  @Test
  public void testGetParticipantInfo() {
    final Predictor newPredictor = Predictor.createPredictor("10", "10", this.owner, Boolean.TRUE);
    newPredictor.newInvitation(this.participantTwo.getId());
    assertEquals(this.participantTwo.getEmail(),
        newPredictor.participantInfo(this.participantTwo.getId()));
  }

}
