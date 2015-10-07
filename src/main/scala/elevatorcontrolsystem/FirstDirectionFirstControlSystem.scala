package elevatorcontrolsystem

import scala.collection.mutable.Queue

import ElevatorControlSystem._

/**
 * Chooses a pickup floor based on the direction of the
 * oldest unfulfilled request. Chooses the lowest Up request
 * or the highest Down request for the next pickup point.
 */
class FirstDirectionFirstControlSystem extends BaseControlSystem {

  val q = Queue.empty[PickupRequest]

  override def requestPickup(floor: Int, direction: Direction) =
    q.enqueue(PickupRequest(floor, direction))

  override def update(status: ElevatorStatus) = {
    super.update(status)
    q.dequeueAll(_.isPickupDone(status))
  }

  override def getFreeCommand(s: ElevatorStatus) =
    if (!q.isEmpty)
      q.head.direction match {
        case Up =>
          moveToPickup(s, lowestUpRequest.floor, Up)
        case Down =>
          moveToPickup(s, highestDownRequest.floor, Down)
      }
    else
      OpenDoor(Up)

  override def isAtPickup(s: ElevatorStatus) =
    q.contains(PickupRequest(s.floor, s.direction.get))

  def lowestUpRequest =
    q.filter(_.direction == Up)
      .sortBy(_.floor)
      .head

  def highestDownRequest =
    q.filter(_.direction == Down)
      .sortBy(-_.floor)
      .head

}