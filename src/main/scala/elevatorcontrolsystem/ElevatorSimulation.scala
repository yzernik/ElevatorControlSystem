package elevatorcontrolsystem

import ElevatorControlSystem._

/**
 * A Simulation of some elevators and passengers using the
 * ElevatorControlSystem implementation.
 *
 */
object ElevatorSimulation extends App {

  val NUMFLOORS = 10

  val elevators = (1 to 3).map(new Elevator(_, NUMFLOORS))

  //val system = new FirstComeControlSystem(elevators)
  val system = new FirstDirectionFirstControlSystem(elevators)

  val building = new Building(system, elevators)

  building.addPassenger(Passenger("p1", 8, 5))
  building.addPassenger(Passenger("p2", 1, 9))
  building.addPassenger(Passenger("p3", 5, 6))
  building.addPassenger(Passenger("p4", 6, 1))

  while (true) {
    println(system.status)
    system.step
    building.update
    readLine()
  }

}

class Building(system: ElevatorControlSystem,
               elevators: Seq[Elevator]) {

  var waitingPassengers = Set.empty[Passenger]

  def addPassenger(p: Passenger) = {
    system.requestPickup(p.start, p.direction)
    waitingPassengers += p
  }

  def update =
    elevators
      .filter(_.isLoading)
      .foreach { e =>
        unloadElevator(e)
        loadElevator(e)
      }

  def loadElevator(e: Elevator) = {
    val loaded = e.load(waitingPassengers.toSeq)
    waitingPassengers --= loaded
  }

  def unloadElevator(e: Elevator) =
    e.unload.foreach { p =>
      println(s"$p arrived")
    }

}

class Elevator(id: Int, numFloors: Int) extends ElevatorControl {

  private var currentFloor: Int = 0
  private var passengers: Set[Passenger] = Set.empty
  private var loadingDirection: Option[Direction] = None

  override def move(direction: Direction) = {
    loadingDirection = None
    direction match {
      case Up =>
        if (currentFloor < numFloors - 1)
          currentFloor += 1
      case Down =>
        if (currentFloor > 0)
          currentFloor -= 1
    }
  }

  override def openDoor(direction: Direction) =
    loadingDirection = Some(direction)

  override def status =
    ElevatorStatus(currentFloor, goalFloors, direction, isLoading)

  /* Returns the passengers that were loaded */
  def load(ps: Seq[Passenger]): Seq[Passenger] = {
    val toLoad = ps
      .filter(_.start == currentFloor)
      .filter(_.direction == loadingDirection.get)
    passengers ++= toLoad
    toLoad
  }

  /* Returns the passengers that were unloaded */
  def unload: Seq[Passenger] = {
    val toUnload = passengers.filter(_.destination == currentFloor)
    passengers --= toUnload
    toUnload.toSeq
  }

  def isLoading =
    loadingDirection.isDefined

  def goalFloors =
    passengers.map(_.destination).toList.sorted

  def direction =
    passengers.map(_.direction).headOption

}

case class Passenger(name: String, start: Int, destination: Int) {
  require(destination != start, "Passenger should be going somewhere")
  def direction: Direction =
    if (destination > start) Up else Down
}