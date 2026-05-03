# 1. Aşama: Build
FROM node:20-alpine AS build
WORKDIR /app

# build-arg ekliyoruz: Bu, dışarıdan (compose dosyasından) URL'yi almamızı sağlar
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

COPY package*.json ./
RUN npm install
COPY . .

# build komutu çalıştığında Vite bu değişkeni kodun içine gömer
RUN npm run build

# 2. Aşama: Serve
FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=build /app/dist ./dist
EXPOSE 5173

# 'serve' komutu ile dist klasörünü 5173 portundan sunuyoruz
CMD ["serve", "-s", "dist", "-l", "5173"]