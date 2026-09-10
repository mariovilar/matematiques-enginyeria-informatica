import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import GameBoard from '@/components/GameBoard.vue'

// Mock the image imports and assetHelper
vi.mock('@/assets/SeaWarfareSet/HitOrMiss/hitBig.png', () => ({ default: 'mocked-hit-image' }))
vi.mock('@/assets/SeaWarfareSet/HitOrMiss/missBig.png', () => ({ default: 'mocked-miss-image' }))
vi.mock('@/components/assetHelper.js', () => ({ default: vi.fn(() => 'mocked-ship-image') }))

describe('GameBoard - Critical Tests', () => {
  const createMockBoard = () => Array(10).fill().map(() => Array(10).fill(0))

  it('renders 10x10 board correctly', () => {
    const wrapper = mount(GameBoard, {
      props: {
        board: createMockBoard(),
        ships: []
      }
    })

    // Critical: Check board structure
    expect(wrapper.find('.game-board-container').exists()).toBe(true)
    expect(wrapper.find('.board').exists()).toBe(true)
    
    // Critical: Correct number of cells (10x10 = 100)
    const cells = wrapper.findAll('.board-cell')
    expect(cells.length).toBe(100)
    
    // Critical: Column headers A-J
    const headers = wrapper.findAll('th.board-header')
    expect(headers.length).toBe(10) // A, B, C, D, E, F, G, H, I, J
  })

  it('handles cell clicks correctly', async () => {
    const wrapper = mount(GameBoard, {
      props: {
        board: createMockBoard(),
        ships: []
      }
    })

    // Critical: Cell click emits correct coordinates
    const firstCell = wrapper.findAll('.board-cell')[0] // Position 0,0
    await firstCell.trigger('click')
    
    expect(wrapper.emitted('cell-click')).toBeTruthy()
    expect(wrapper.emitted('cell-click')[0]).toEqual([0, 0])
  })

  it('displays hit markers correctly', () => {
    const boardWithHit = createMockBoard()
    boardWithHit[2][3] = -1 // Hit at position 2,3

    const wrapper = mount(GameBoard, {
      props: {
        board: boardWithHit,
        ships: []
      }
    })

    // Critical: Hit marker is shown
    expect(wrapper.find('.hit-marker').exists()).toBe(true)
    expect(wrapper.find('.hit-image').exists()).toBe(true)
  })

  it('displays miss markers correctly', () => {
    const boardWithMiss = createMockBoard()
    boardWithMiss[1][4] = 11 // Miss at position 1,4

    const wrapper = mount(GameBoard, {
      props: {
        board: boardWithMiss,
        ships: []
      }
    })

    // Critical: Miss marker is shown
    expect(wrapper.find('.miss-marker').exists()).toBe(true)
    expect(wrapper.find('.miss-image').exists()).toBe(true)
  })

  it('shows ships when not hidden', () => {
    const boardWithShip = createMockBoard()
    boardWithShip[0][0] = 1 // Ship part

    const wrapper = mount(GameBoard, {
      props: {
        board: boardWithShip,
        ships: [],
        hidden: false
      }
    })

    // Critical: Ship is visible on own board
    const cellWithShip = wrapper.findAll('.board-cell')[0]
    expect(cellWithShip.classes()).toContain('has-ship')
  })

  it('hides ships when hidden prop is true', () => {
    const boardWithShip = createMockBoard()
    boardWithShip[0][0] = 1 // Ship part

    const wrapper = mount(GameBoard, {
      props: {
        board: boardWithShip,
        ships: [],
        hidden: true // Enemy board
      }
    })

    // Critical: Ship is hidden on enemy board
    const cellWithShip = wrapper.findAll('.board-cell')[0]
    expect(cellWithShip.classes()).not.toContain('has-ship')
  })
})
