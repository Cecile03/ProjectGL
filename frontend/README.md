# frontend

## Jump into library

```sh
cd frontend
```

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

## Project Structure

### Components Directory

This directory is intended to contain the created components that can be reused in various parts of the application.

### Services Directory

The services directory contains TypeScript files where all functions related to the application's API will be exported.

### Views Directory

The views directory contains all the pages of the application. Within this directory, you may also find a "components" subdirectory containing components used exclusively on that page.

Structure exemple :

<!-- prettier-ignore -->
```plaintext
views/
├── page1/
│   ├── components/
│   │   ├── component1.vue
│   │   └── component2.vue
│   └── page1.vue
├── page2/
│   ├── components/
│   └── page2.vue
└── page3/
    ├── components/
    └── page3.vue

