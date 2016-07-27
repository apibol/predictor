package predictor.domain;

import domain.Participant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import predictor.domain.exception.InvalidHash;
import predictor.domain.exception.ParticipantNotInvited;
import predictor.domain.specification.IsParticipantInvited;
import predictor.domain.specification.IsPrivatePredictor;
import predictor.domain.specification.IsValidJoinPredictionRequest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * It represents a group of participants grouped in on event.
 *
 * @author Claudio E. de Oliveira on 10/03/16.
 */
@Data
@Document(collection = "predictor")
public class Predictor {

  @Id
  private String id;

  private String eventId;

  private Boolean open = Boolean.FALSE;

  /**
   * Accepted participants
   */
  private Set<Participant> participants = new HashSet<>();

  /**
   * Invitations
   */
  private Set<Invitation> invitations = new HashSet<>();

  /**
   * Accepted invitations
   */
  private Set<AcceptedInvitation> acceptedInvitations = new HashSet<>();

  /**
   * Owner
   */
  private Participant owner;

  /**
   * Default constructor
   */
  Predictor() {}

  /**
   * Constructor with owner
   *
   * @param eventId
   * @param open
   * @param owner
   */
  public Predictor(String id, String eventId, Boolean open, Participant owner) {
    this.id = id;
    this.eventId = eventId;
    this.open = open;
    this.owner = owner;
  }

  /**
   * Constructor
   *
   * @param eventId
   * @param participant
   */
  private Predictor(String id, String eventId, Participant participant, Boolean open) {
    this.id = id;
    this.eventId = eventId;
    this.open = open;
    final Invitation invitation = this.newInvitation(participant.getId());
    join(JoinPredictorRequest.create(participant, invitation.getKey()));
  }

  /**
   * Factory method
   *
   * @param eventId
   * @param open
   * @param owner
   * @return
   */
  public static Predictor createPredictor(String id, String eventId, Boolean open,
      Participant owner) {
    return new Predictor(id, eventId, open, owner);
  }

  /**
   * Factory method
   *
   * @param eventId
   * @param participant
   * @return
   */
  public static Predictor createPredictor(String id, String eventId, Participant participant,Boolean open) {
    return new Predictor(id, eventId, participant, open);
  }

  /**
   * Add participant in predictor
   *
   * @param joinPredictorRequest
   * @return
   */
  public Predictor join(JoinPredictorRequest joinPredictorRequest) throws ParticipantNotInvited {
    if (new IsPrivatePredictor().isSatisfiedBy(this)) {
      if (new IsParticipantInvited(joinPredictorRequest.getParticipantId()).isSatisfiedBy(this)) {
        if (new IsValidJoinPredictionRequest(this).isSatisfiedBy(joinPredictorRequest)) {
          this.removeInvitation(joinPredictorRequest.getParticipantId());
          this.acceptedInvitations
              .add(AcceptedInvitation.createNew(joinPredictorRequest.getParticipantId()));
        } else {
          throw new InvalidHash(joinPredictorRequest.getParticipantId());
        }
      } else {
        throw new ParticipantNotInvited(joinPredictorRequest.getParticipantId(), this.id);
      }
    }
    this.participants.add(joinPredictorRequest.getParticipant());
    return this;
  }

  /**
   * Remove participant from predictor
   *
   * @param participant
   * @return
   */
  public Predictor removeParticipant(Participant participant) {
    this.participants.remove(participant);
    return this;
  }

  /**
   * Get participant info
   *
   * @param participantId
   * @return
   */
  public Participant participantInfo(String participantId) {
    return this.participants.stream().filter(part -> part.getId().equals(participantId)).findFirst()
        .get();
  }

  /**
   * Remove invitation
   *
   * @param userId
   * @return
   */
  private Predictor removeInvitation(String userId) {
    Iterator<Invitation> iterator = this.invitations.iterator();
    while (iterator.hasNext()) {
      Invitation invitation = iterator.next();
      if (invitation.getUserId().equals(userId)) {
        iterator.remove();
      }
    }
    return this;
  }

  /**
   * Add new invitation
   *
   * @param userId
   * @return
   */
  public Invitation newInvitation(String userId) {
    final Invitation newInvitation = Invitation.createNew(userId);
    this.invitations.add(newInvitation);
    return newInvitation;
  }

}
