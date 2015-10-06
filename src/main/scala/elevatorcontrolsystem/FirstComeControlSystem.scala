package elevatorcontrolsystem

import scala.collection.mutable.Queue

import ElevatorControlSystem._

/**
 * Uses a First-Come-First-Serve strategy.
 * Each free elevators moves to pickup the earliest request first.
 */
class FirstComeControlSystem(elevators: Seq[ElevatorControl])
    extends BaseControlSystem(elevators) {

  val q = Queue.empty[PickupRequest]

  override def requestPickup(floor: Int, direction: Direction) =
    q.enqueue(PickupRequest(floor, direction))

  override def update(status: ElevatorStatus) =
    q.dequeueAll(_.isPickupDone(status))

  override def moveFreeElevator(e: ElevatorControl) =
    if (!q.isEmpty)
      moveToPickup(e, q.head.floor, q.head.direction)

  override def isAtPickup(e: ElevatorControl) =
    q.contains(PickupRequest(e.status.floor, e.status.direction.get))

}