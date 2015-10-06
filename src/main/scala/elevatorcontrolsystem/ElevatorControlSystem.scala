package elevatorcontrolsystem

import ElevatorControlSystem._

/**
 * Interface for the Elevator Control System.
 * Can be used to time-step by calling the step method.
 */
trait ElevatorControlSystem {
  def status: Seq[ElevatorStatus]
  def update(status: ElevatorStatus): Unit
  def requestPickup(floor: Int, direction: Direction): Unit
  def step: Unit
}

object ElevatorControlSystem {

  case class ElevatorStatus(floor: Int,
                            goals: List[Int],
                            direction: Option[Direction],
                            isLoading: Boolean)

  case class PickupRequest(floor: Int, direction: Direction) {
    def isPickupDone(status: ElevatorStatus) =
      status.floor == floor &&
        status.direction == Some(direction) &&
        status.isLoading
  }

  sealed trait Direction
  case object Up extends Direction
  case object Down extends Direction

  trait ElevatorControl {
    def move(direction: Direction): Unit
    def openDoor(direction: Direction): Unit
    def status: ElevatorStatus
  }

}

/**
 * Partial implementation of ElevatorControlSystem
 * Contains logic for moving elevators Up and Down
 * Does not implement the pickup scheduling strategy.
 */
abstract class BaseControlSystem(elevators: Seq[ElevatorControl]) extends ElevatorControlSystem {

  override def status =
    elevators.map(_.status)

  override def step = {
    elevators.foreach(updateStatus)
    elevators.foreach(moveElevator)
  }

  def moveElevator(e: ElevatorControl) =
    if (!e.status.goals.isEmpty)
      moveBusyElevator(e)
    else
      moveFreeElevator(e)

  def moveBusyElevator(e: ElevatorControl) =
    if (isAtGoal(e) || isAtPickup(e))
      e.openDoor(e.status.direction.get)
    else
      e.move(e.status.direction.get)

  def moveToPickup(e: ElevatorControl, floor: Int, direction: Direction) =
    if (floor > e.status.floor)
      e.move(Up)
    else if (floor < e.status.floor)
      e.move(Down)
    else
      e.openDoor(direction)

  def isAtGoal(e: ElevatorControl) =
    e.status.goals.contains(e.status.floor)

  def updateStatus(e: ElevatorControl) =
    update(e.status)

  def moveFreeElevator(e: ElevatorControl): Unit

  def isAtPickup(e: ElevatorControl): Boolean

}
