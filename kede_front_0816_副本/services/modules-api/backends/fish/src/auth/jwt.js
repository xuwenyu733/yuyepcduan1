const jwt = require('jsonwebtoken')

const ACCESS_SECRET = process.env.JWT_ACCESS_SECRET || 'fish-disease-access-secret'
const REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'fish-disease-refresh-secret'
const ACCESS_EXPIRES_IN = process.env.JWT_ACCESS_EXPIRES || '2h'
const REFRESH_EXPIRES_IN = process.env.JWT_REFRESH_EXPIRES || '7d'

function signAccessToken(user) {
  return jwt.sign(
    {
      sub: user.id,
      username: user.username,
      role: user.role,
      type: 'access'
    },
    ACCESS_SECRET,
    { expiresIn: ACCESS_EXPIRES_IN }
  )
}

function signRefreshToken(user) {
  return jwt.sign(
    {
      sub: user.id,
      username: user.username,
      role: user.role,
      type: 'refresh'
    },
    REFRESH_SECRET,
    { expiresIn: REFRESH_EXPIRES_IN }
  )
}

function verifyAccessToken(token) {
  const payload = jwt.verify(token, ACCESS_SECRET)
  if (payload.type !== 'access') {
    const err = new Error('无效的 access token')
    err.name = 'JsonWebTokenError'
    throw err
  }
  return payload
}

function verifyRefreshToken(token) {
  const payload = jwt.verify(token, REFRESH_SECRET)
  if (payload.type !== 'refresh') {
    const err = new Error('无效的 refresh token')
    err.name = 'JsonWebTokenError'
    throw err
  }
  return payload
}

function issueTokenPair(user) {
  return {
    accessToken: signAccessToken(user),
    refreshToken: signRefreshToken(user),
    tokenType: 'Bearer',
    expiresIn: ACCESS_EXPIRES_IN
  }
}

module.exports = {
  signAccessToken,
  signRefreshToken,
  verifyAccessToken,
  verifyRefreshToken,
  issueTokenPair
}
