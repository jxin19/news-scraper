FROM node:18-alpine AS base
RUN apk add --no-cache libc6-compat
WORKDIR /app
ENV NEXT_TELEMETRY_DISABLED 1

FROM base AS deps
COPY package*.json ./
RUN npm ci --only=production

FROM base AS deps-dev
COPY package*.json ./
RUN npm ci

FROM base AS builder
COPY --from=deps-dev /app/node_modules ./node_modules
COPY . .

ENV ESLINT_NO_DEV_ERRORS=true
ENV NEXT_IGNORE_ESLINT_ERRORS=true
ENV API_URL=http://ddi-api:8080

RUN npm run build

FROM base AS runner
ENV NODE_ENV production
ENV API_URL=http://ddi-api:8080

# nextjs 사용자와 그룹 생성
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# 빌드된 파일들 복사
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
ENV PORT 3000
ENV HOSTNAME "0.0.0.0"

CMD ["node", "server.js"]