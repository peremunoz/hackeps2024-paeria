import { initializeApp } from 'firebase/app'
import { getAnalytics } from 'firebase/analytics'

const firebaseConfig = {
  apiKey: "AIzaSyAJu64y_dQDaNKrxb13UUgYDJFyUV8bzeQ",
  authDomain: "aparca-ya.firebaseapp.com",
  projectId: "aparca-ya",
  storageBucket: "aparca-ya.firebasestorage.app",
  messagingSenderId: "490106851491",
  appId: "1:490106851491:web:48c4134b7e1c45cafa6f16",
  measurementId: "G-SJMYV1K0PP"
}

export const app = initializeApp(firebaseConfig)
export const analytics = getAnalytics(app)