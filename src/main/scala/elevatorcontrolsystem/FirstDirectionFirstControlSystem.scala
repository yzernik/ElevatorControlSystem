package elevatorcontrolsystem

import scala.collection.mutable.Queue

import ElevatorControlSystem._

/**
 * Chooses a pickup floor based on the direction of the
 * oldest unfulfilled request. Chooses the lowest Up request
 * or the highest Down request for the next pickup point.
 */
class FirstDirectionFirstControlSystem(elevators: Seq[ElevatorControl])
    extends BaseControlSystem(elevators) {

  val q = Queue.empty[PickupRequest]

  override def requestPickup(floor: Int, direction: Direction) =
    q.enqueue(PickupRequest(floor, direction))

  override def update(status: ElevatorStatus) =
    q.dequeueAll(_.isPickupDone(status))

  override def moveFreeElevator(e: ElevatorControl) =
    if (!q.isEmpty)
      q.head.direction match {
        case Up =>
          moveToPickup(e, lowestUpRequest.floor, Up)
        case Down =>
          moveToPickup(e, highestDownRequest.floor, Down)
      }

  override def isAtPickup(e: ElevatorControl) =
    q.contains(PickupRequest(e.status.floor, e.status.direction.get))

  def lowestUpRequest =
    q.filter(_.direction == Up)
      .sortBy(_.floor)
      .head

  def highestDownRequest =
    q.filter(_.direction == Down)
      .sortBy(-_.floor)
      .head

}