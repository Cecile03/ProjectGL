const DEV_CONFIG = {
  apiUrl: 'http://localhost:8080/api',
};

const PROD_CONFIG = {
  //apiUrl: 'https://172.24.1.18/api',
  apiUrl: `${window.location.origin}/api`,
};

export const CONFIG =
  process.env.NODE_ENV === 'production' ? PROD_CONFIG : DEV_CONFIG;
