package elevatorcontrolsystem

import scala.collection.mutable.Queue

import ElevatorControlSystem._

/**
 * Uses a First-Come-First-Serve strategy.
 * Each free elevators moves to pickup the earliest request first.
 */
class FirstComeControlSystem extends BaseControlSystem {

  val q = Queue.empty[PickupRequest]

  override def requestPickup(floor: Int, direction: Direction) =
    q.enqueue(PickupRequest(floor, direction))

  override def update(status: ElevatorStatus) = {
    super.update(status)
    q.dequeueAll(_.isPickupDone(status))
  }

  override def getFreeCommand(s: ElevatorStatus) =
    if (!q.isEmpty)
      moveToPickup(s, q.head.floor, q.head.direction)
    else OpenDoor(Up)

  override def isAtPickup(s: ElevatorStatus) =
    q.contains(PickupRequest(s.floor, s.direction.get))

}