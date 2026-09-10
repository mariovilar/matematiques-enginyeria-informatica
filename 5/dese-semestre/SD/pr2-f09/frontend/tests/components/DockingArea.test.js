import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DockingArea from '@/components/DockingArea.vue'

// Mock the assetHelper
vi.mock('@/components/assetHelper', () => ({ default: vi.fn(() => 'mocked-ship-image') }))

describe('DockingArea - Critical Ship Placement Tests', () => {
  const mockShips = [
    { type: 1, size: 1, placed: false },
    { type: 2, size: 2, placed: false },
    { type: 3, size: 3, placed: true }, // This one is already placed
  ]

  it('renders all available ships', () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    // Critical: All ships should be rendered
    const dockedShips = wrapper.findAll('.docked-ship')
    expect(dockedShips.length).toBe(3)

    // Critical: Ship names should be displayed
    expect(wrapper.text()).toContain('Patrol Boat (1)')
    expect(wrapper.text()).toContain('Destroyer (2)')
    expect(wrapper.text()).toContain('Cruiser (3)')
  })

  it('allows ship selection', async () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    // Critical: Click on first ship to select it
    const firstShip = wrapper.findAll('.docked-ship')[0]
    await firstShip.trigger('click')

    // Critical: Should emit ship-selected event
    expect(wrapper.emitted('ship-selected')).toBeTruthy()
    expect(wrapper.emitted('ship-selected')[0][0]).toEqual(
      expect.objectContaining({
        type: mockShips[0].type,
        size: mockShips[0].size,
        placed: mockShips[0].placed
      })
    )

    // Critical: Selected ship should have 'selected' class
    expect(firstShip.classes()).toContain('selected')
  })

  it('rotate button is disabled when no ship selected', () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    const rotateButton = wrapper.find('button')
    
    // Critical: Rotate button should be disabled initially
    expect(rotateButton.element.disabled).toBe(true)
  })

  it('rotate button becomes enabled when ship is selected', async () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    // Select a ship first
    const firstShip = wrapper.findAll('.docked-ship')[0]
    await firstShip.trigger('click')

    const rotateButton = wrapper.find('button')
    
    // Critical: Rotate button should be enabled after selection
    expect(rotateButton.element.disabled).toBe(false)
  })

  it('emits rotate-ship event when rotate button is clicked', async () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    // Select a ship first
    const firstShip = wrapper.findAll('.docked-ship')[0]
    await firstShip.trigger('click')

    // Click rotate button
    const rotateButton = wrapper.find('button')
    await rotateButton.trigger('click')

    // Critical: Should emit rotate-ship event
    expect(wrapper.emitted('rotate-ship')).toBeTruthy()
  })

  it('shows correct ship names', () => {
    const wrapper = mount(DockingArea, {
      props: { ships: mockShips }
    })

    const shipNames = wrapper.findAll('.ship-name')
    
    // Critical: Ship names should match the expected format
    expect(shipNames[0].text()).toBe('Patrol Boat (1)')
    expect(shipNames[1].text()).toBe('Destroyer (2)')
    expect(shipNames[2].text()).toBe('Cruiser (3)')
  })
})
