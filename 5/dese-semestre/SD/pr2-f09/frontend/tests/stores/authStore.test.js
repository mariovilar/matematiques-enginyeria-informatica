import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/store/authStore.js'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
vi.stubGlobal('localStorage', localStorageMock)

// Mock AuthService
vi.mock('@/services/auth', () => ({
  default: {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
  }
}))

// Mock GameStore
vi.mock('@/store/index.js', () => ({
  useGameStore: vi.fn(() => ({
    clearGameData: vi.fn(),
    cleanValues: vi.fn() // Add the missing method
  }))
}))

describe('AuthStore - Critical Authentication Tests', () => {
  let authStore

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    vi.clearAllMocks()
  })

  describe('State Management', () => {
    it('initializes with correct default state', () => {
      expect(authStore.username).toBe(null)
      expect(authStore.accessToken).toBe(null)
      expect(authStore.refreshToken).toBe(null)
      expect(authStore.isAuthenticated).toBe(false)
      expect(authStore.loading).toBe(false)
      expect(authStore.error).toBe(null)
      expect(authStore.playersList).toEqual([])
    })

    it('loads authentication data from localStorage', () => {
      // Mock localStorage data
      localStorageMock.getItem.mockImplementation((key) => {
        switch (key) {
          case 'username': return 'testuser'
          case 'access': return 'fake-access-token'
          case 'refresh': return 'fake-refresh-token'
          default: return null
        }
      })

      authStore.initializeAuthStore()

      expect(authStore.username).toBe('testuser')
      expect(authStore.accessToken).toBe('fake-access-token')
      expect(authStore.refreshToken).toBe('fake-refresh-token')
      expect(authStore.isAuthenticated).toBe(true)
    })
  })

  describe('Login Process', () => {
    it('handles successful login', async () => {
      const mockResponse = {
        data: {
          access: 'new-access-token',
          refresh: 'new-refresh-token'
        }
      }

      // Mock successful login
      const AuthService = await import('@/services/auth')
      AuthService.default.login.mockResolvedValue(mockResponse)
      
      // Mock getUserIdByUsername to avoid API call
      authStore.getUserIdByUsername = vi.fn().mockResolvedValue('123')

      const userCredentials = { username: 'testuser', password: 'testpass' }
      
      await authStore.login(userCredentials)

      // Critical: Authentication state should be updated
      expect(authStore.username).toBe('testuser')
      expect(authStore.accessToken).toBe('new-access-token')
      expect(authStore.refreshToken).toBe('new-refresh-token')
      expect(authStore.isAuthenticated).toBe(true)
      expect(authStore.loading).toBe(false)
      expect(authStore.error).toBe(null)

      // Critical: Data should be saved to localStorage
      expect(localStorageMock.setItem).toHaveBeenCalledWith('username', 'testuser')
      expect(localStorageMock.setItem).toHaveBeenCalledWith('access', 'new-access-token')
      expect(localStorageMock.setItem).toHaveBeenCalledWith('refresh', 'new-refresh-token')
      expect(localStorageMock.setItem).toHaveBeenCalledWith('user_id', '123')
    })

    it('handles login failure', async () => {
      const mockError = {
        response: {
          data: {
            detail: 'Invalid credentials'
          }
        }
      }

      const AuthService = await import('@/services/auth')
      AuthService.default.login.mockRejectedValue(mockError)

      const userCredentials = { username: 'baduser', password: 'badpass' }
      
      await authStore.login(userCredentials)

      // Critical: Error state should be set
      expect(authStore.error).toBe('Invalid credentials')
      expect(authStore.isAuthenticated).toBe(false)
      expect(authStore.loading).toBe(false)
    })
  })

  describe('Logout Process', () => {
    it('clears authentication data on logout', () => {
      // Set initial authenticated state
      authStore.username = 'testuser'
      authStore.accessToken = 'token'
      authStore.refreshToken = 'refresh'
      authStore.isAuthenticated = true

      authStore.logout()

      // Critical: isAuthenticated should be false
      expect(authStore.isAuthenticated).toBe(false)

      // Critical: localStorage should be cleared
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('username')
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('access')
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('refresh')
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('user_id')
    })
  })
})
