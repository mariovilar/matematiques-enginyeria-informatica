<template>
	<div class="docking-area">
		<div class="rotate-control text-center mb-3">
			<button class="btn btn-outline-primary" @click="rotateSelectedShip" :disabled="!hasSelectedShip">
				Rotate Ship
			</button>
		</div>

		<div class="ships-container">
			<div v-for="ship in ships" :key="`dock-ship-${ship.type}`" class="docked-ship"
				:class="{ selected: selectedShipType === ship.type }" @click="selectShip(ship)">
				<div class="ship-image-container" :style="getShipContainerStyle(ship)">
					<img :src="getImagePath(ship.type)" :alt="`Ship type ${ship.type}`" class="ship-image"
						:style="getShipImageStyle(ship)" />
				</div>
				<div class="ship-name">{{ getShipName(ship.type) }}</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed } from "vue";
import getImagePath from "./assetHelper";

const props = defineProps({
	ships: {
		type: Array,
		required: true,
	},
});

const emit = defineEmits(["ship-selected", "rotate-ship"]);

const selectedShipType = ref(null);
const shipRotations = ref({}); // Track rotation state of each ship

const hasSelectedShip = computed(() => {
	return selectedShipType.value !== null;
});

// Ship type to name mapping
const shipNames = {
	1: "Patrol Boat (1)",
	2: "Destroyer (2)",
	3: "Cruiser (3)",
	4: "Submarine (4)",
	5: "Carrier (5)",
};

// Get ship name from type
const getShipName = (type) => {
	return shipNames[type] || `Ship Type ${type}`;
};

// Select a ship from the dock
const selectShip = (ship) => {
	selectedShipType.value = ship.type;

	// Initialize rotation state if not already set
	if (shipRotations.value[ship.type] === undefined) {
		shipRotations.value[ship.type] = ship.isVertical === false;
	}

	// Update ship object with current rotation
	const updatedShip = {
		...ship,
		isVertical: !shipRotations.value[ship.type],
	};

	emit("ship-selected", updatedShip);
};

// Rotate the selected ship
const rotateSelectedShip = () => {
	if (selectedShipType.value) {
		// Toggle rotation state for this ship
		shipRotations.value[selectedShipType.value] =
			!shipRotations.value[selectedShipType.value];

		// Find the selected ship
		const ship = props.ships.find((s) => s.type === selectedShipType.value);
		if (ship) {
			// Update ship object with new rotation
			const updatedShip = {
				...ship,
				isVertical: !shipRotations.value[selectedShipType.value],
			};

			// Emit event with updated ship
			emit("ship-selected", updatedShip);
			emit("rotate-ship");
		}
	}
};

// Get container style based on ship size and rotation
const getShipContainerStyle = (ship) => {
	const isRotated = shipRotations.value[ship.type];
	const size = ship.size * 50; // 50px per cell

	if (isRotated) {
		return {
			width: `${size}px`,
			height: "50px",
		};
	} else {
		return {
			width: "50px",
			height: `${size}px`,
		};
	}
};

// Get image style based on rotation
const getShipImageStyle = (ship) => {
	const isRotated = shipRotations.value[ship.type];

	if (isRotated) {
		return {
			width: "100%",
			height: "100%",
			objectFit: "contain",
			transform: "rotate(90deg)",
			transformOrigin: "center",
		};
	} else {
		return {
			width: "100%",
			height: "100%",
			objectFit: "contain",
		};
	}
};
</script>

<style scoped>
.docking-area {
	background-color: #24497a;
	border-radius: 10px;
	padding: 15px;
	min-height: 400px;
	box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.5);
}

.ships-container {
	display: flex;
	flex-direction: column;
	gap: 20px;
	align-items: center;
	max-height: 350px;
	overflow: auto;
}

.docked-ship {
	background-color: rgba(255, 255, 255, 0.1);
	border-radius: 5px;
	padding: 10px;
	cursor: pointer;
	width: 100%;
	text-align: center;
	transition: all 0.2s ease;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.docked-ship:hover {
	background-color: rgba(255, 255, 255, 0.2);
}

.docked-ship.selected {
	background-color: rgba(255, 255, 255, 0.3);
	box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.ship-image-container {
	margin: 0 auto;
	background-color: rgba(0, 0, 0, 0.2);
	display: flex;
	justify-content: center;
	align-items: center;
	transition: all 0.3s ease;
}

.ship-image {
	width: 100%;
	height: 100%;
	object-fit: contain;
	transition: transform 0.3s ease;
}

.ship-name {
	color: white;
	margin-top: 5px;
	font-size: 0.9rem;
}

.btn {
	color: white;
}
</style>
