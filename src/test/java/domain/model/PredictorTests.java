package domain.model;

import domain.Participant;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import predictor.domain.Invitation;
import predictor.domain.JoinPredictorRequest;
import predictor.domain.Predictor;
import predictor.domain.exception.InvalidHash;
import predictor.domain.exception.ParticipantNotInvited;

import java.security.Security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The predictor tests
 *
 * @author Claudio E. de Oliveira on on 25/07/16.
 */
public class PredictorTests {

  /**
   * Bouncy Castle Provider
   */
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
    final Invitation newInvitation = newPredictor.newInvitation(this.participantTwo.getId());
    assertTrue(newPredictor.getInvitations().size() == 2);
    assertEquals(this.participantTwo.getId(), newInvitation.getUserId());
  }

  @Test
  public void testJoinPredictorWithSuccess() {
    final Predictor newPredictor = Predictor.createPredictor("10", "10", this.owner, Boolean.TRUE);
    final Invitation newInvitation = newPredictor.newInvitation(this.participantTwo.getId());
    final JoinPredictorRequest joinPredictorRequest = JoinPredictorRequest.create(this.participantTwo, newInvitation.getKey());
    final Predictor predictorWithUser = newPredictor.join(joinPredictorRequest);
    assertEquals(this.participantTwo.getEmail(),predictorWithUser.participantInfo(this.participantTwo.getId()).getEmail());
  }

  @Test(expected = ParticipantNotInvited.class)
  public void testJoinPredictorNotInvited() {
    final Predictor newPredictor = Predictor.createPredictor("10", "10", this.owner, Boolean.FALSE);
    final Invitation newInvitation = newPredictor.newInvitation(this.participantTwo.getId());
    final JoinPredictorRequest joinPredictorRequest = JoinPredictorRequest.create(this.participantThree, newInvitation.getKey());
    final Predictor predictorWithUser = newPredictor.join(joinPredictorRequest);
  }

  @Test(expected = InvalidHash.class)
  public void testJoinPredictorWithHashInvalid() {
    final Predictor newPredictor = Predictor.createPredictor("10", "10", this.owner, Boolean.FALSE);
    final Invitation newInvitation = newPredictor.newInvitation(this.participantTwo.getId());
    final JoinPredictorRequest joinPredictorRequest = JoinPredictorRequest.create(this.participantTwo, "XXXXXX");
    final Predictor predictorWithUser = newPredictor.join(joinPredictorRequest);
  }

}
