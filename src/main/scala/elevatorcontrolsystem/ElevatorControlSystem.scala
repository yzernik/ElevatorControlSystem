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
  def getCommand(id: Int): ElevatorCommand
}

object ElevatorControlSystem {

  case class ElevatorStatus(id: Int,
                            floor: Int,
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

  sealed trait ElevatorCommand
  case class Move(direction: Direction) extends ElevatorCommand
  case class OpenDoor(direction: Direction) extends ElevatorCommand

}

/**
 * Partial implementation of ElevatorControlSystem
 * Contains logic for moving elevators Up and Down
 * Does not implement the pickup scheduling strategy.
 */
abstract class BaseControlSystem extends ElevatorControlSystem {

  private var elevators: Map[Int, ElevatorStatus] = Map.empty

  override def status =
    elevators.values.toVector

  override def getCommand(id: Int) = {
    val s = elevators.get(id).get
    if (!s.goals.isEmpty)
      getBusyCommand(s)
    else
      getFreeCommand(s)
  }

  override def update(status: ElevatorStatus) =
    elevators += status.id -> status

  def getBusyCommand(s: ElevatorStatus) =
    if (isAtGoal(s) || isAtPickup(s))
      OpenDoor(s.direction.get)
    else
      Move(s.direction.get)

  def getFreeCommand(s: ElevatorStatus): ElevatorCommand

  def moveToPickup(s: ElevatorStatus, floor: Int, direction: Direction) =
    if (floor > s.floor)
      Move(Up)
    else if (floor < s.floor)
      Move(Down)
    else
      OpenDoor(direction)

  def isAtGoal(s: ElevatorStatus) =
    s.goals.contains(s.floor)

  def isAtPickup(s: ElevatorStatus): Boolean

}
