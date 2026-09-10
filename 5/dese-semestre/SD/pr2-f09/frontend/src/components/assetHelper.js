const getImagePath = (shipType) => {
	switch (shipType) {
		case 1:
			return "src/assets/SeaWarfareSet/PatrolBoat/ShipPatrolHull.png";
		case 2:
			return "src/assets/SeaWarfareSet/Destroyer/ShipDestroyerHull.png";
		case 3:
			return "src/assets/SeaWarfareSet/Cruiser/ShipCruiserHull.png";
		case 4:
			return "src/assets/SeaWarfareSet/Submarine/ShipSubMarineHull.png";
		case 5:
			return "src/assets/SeaWarfareSet/Carrier/ShipCarrierHull.png";
		default:
			return "";
	}
};

export default getImagePath;