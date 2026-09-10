<template>
	<div class="game-board-container">
		<table class="board">
			<!-- Column Headers -->
			<thead>
				<tr>
					<th></th>
					<th v-for="col in 10" :key="`header-${col}`" class="board-header">
						{{ String.fromCharCode(64 + col) }}
					</th>
				</tr>
			</thead>
			<tbody>
				<!-- Board Rows -->
				<tr v-for="(row, rowIndex) in board" :key="`row-${rowIndex}`">
					<!-- Row Headers -->
					<td class="board-header">{{ rowIndex + 1 }}</td>

					<!-- Board Cells -->
					<td v-for="(cell, colIndex) in row" :key="`cell-${rowIndex}-${colIndex}`" class="board-cell"
						:class="getCellClass(cell)" @click="$emit('cell-click', rowIndex, colIndex)">
						<!-- Cell content - show hit/miss markers -->
						<div v-if="cell > 10" class="miss-marker">
							<img :src="missImage" class="miss-image" />
						</div>
						<div v-if="cell < 0" class="hit-marker">
							<img :src="hitImage" class="hit-image" />
						</div>
					</td>
				</tr>
			</tbody>
		</table>

		<!-- Render placed ships -->
		<div v-for="ship in ships" :key="`ship-${ship.type}`" class="ship-on-board" :style="getShipStyle(ship)">
			<img :src="getImagePath(ship.type)" :alt="`Ship type ${ship.type}`" :style="getShipImageStyle(ship)" />
		</div>
	</div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import hitImage from "@/assets/SeaWarfareSet/HitOrMiss/hitBig.png";
import missImage from "@/assets/SeaWarfareSet/HitOrMiss/missBig.png";
import getImagePath from "@/components/assetHelper.js";

const props = defineProps({
	board: {
		type: Array,
		required: true,
	},
	ships: {
		type: Array,
		default: () => [],
	},
	hidden: {
		type: Boolean,
		default: false,
	},
});

defineEmits(["cell-click"]);

// Get CSS class for a cell based on its state
const getCellClass = (cell) => {
	const classes = [];
	// On player's board: cells with ship parts (1-5) should show blue background
	// Hidden ships on opponent's board should not show
	if (!props.hidden && cell > 0 && cell < 10) {
		classes.push("has-ship");
	}

	// Hit cells
	if (cell < 0) {
		classes.push("hit");
	}

	// Missed cells
	if (cell > 10) {
		classes.push("miss");
	}

	return classes;
};

// Calculate ship position and style
const getShipStyle = (ship) => {
	if (!ship.position) return {};

	const cellSize = 40; // Cell size in pixels
	return {
		position: "absolute",
		top: `${ship.position.row * cellSize + 31}px`, // +41px to account for the header
		left: `${ship.position.col * cellSize + 31}px`, // +41px to account for the header
		transform: ship.isVertical ? "none" : "rotate(90deg)",
		transformOrigin: ship.isVertical
			? "top left"
			: `${cellSize / 2}px ${cellSize / 2}px`,
		width: `${cellSize}px`,
		height: `${ship.size * cellSize}px`,
		display: props.hidden && !ship.isHit ? "none" : "block",
	};
};

// Style for ship images
const getShipImageStyle = (ship) => {
	return {
		width: "100%",
		height: "100%",
		objectFit: "contain",
	};
};
</script>

<style scoped>
.game-board-container {
	position: relative;
	margin: 0 auto;
}

.board {
	line-height: normal;
	border-collapse: collapse;
	background: url("../assets/SeaWarfareSet/Space/nice-snow.png");
	background-color: #3884e1;
	position: relative;
	border: 2px solid #2465b0;
}

.board-header {
	background-color: #2465b0;
	color: white;
	text-align: center;
	min-width: 30px;
	min-height: 30px;
	width: 30px;
	height: 30px;
	font-weight: bold;
}

.board-cell {
	min-width: 40px;
	min-height: 40px;
	width: 40px;
	height: 40px;
	border: 1px solid rgba(255, 255, 255, 0.3);
	position: relative;
	text-align: center;
	vertical-align: middle;
	cursor: pointer;
}

.board-cell:hover {
	background-color: rgba(255, 255, 255, 0.2);
}

.has-ship {
	background-color: rgba(100, 100, 200, 0.3);
}

.hit-marker {
	position: relative;
	color: red;
	font-size: 2em;
	font-weight: bold;
	line-height: 0.8;
}

.hit-image {
	width: 35px;
	height: 35px;
	position: absolute;
	left: 2px;
	top: -15px;
	z-index: 999;
}

.miss-marker {
	position: relative;
	color: white;
	font-size: 2em;
}

.miss-image {
	width: 35px;
	height: 35px;
	position: absolute;
	left: 2px;
	top: -15px;
	z-index: 999;
}

.ship-on-board {
	pointer-events: none;
	z-index: 10;
}
</style>
