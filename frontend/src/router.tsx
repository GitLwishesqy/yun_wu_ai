import { createBrowserRouter } from 'react-router-dom';
import StudentShell from './components/layout/StudentShell';
import AdminShell from './components/layout/AdminShell';
import Home from './pages/Home';
import Coach from './pages/Coach';
import Scenes from './pages/Scenes';
import ScenesReal from './pages/ScenesReal';
import CoachDirect from './pages/CoachDirect';
import Report from './pages/Report';
import Achievements from './pages/Achievements';
import Profile from './pages/Profile';
import Vocabulary from './pages/Vocabulary';
import Skills from './pages/Skills';
import Incentive from './pages/Incentive';
import Plans from './pages/Plans';
import Corrections from './pages/Corrections';
import Parent from './pages/Parent';
import Admin from './pages/Admin';

export const router = createBrowserRouter([
  { path: '/', element: <Home /> },
  {
    element: <StudentShell />,
    children: [
      { path: '/coach', element: <Coach /> },
      { path: '/coach/:sceneId', element: <CoachDirect /> },
      { path: '/scenes', element: <ScenesReal /> },
      { path: '/report', element: <Report /> },
      { path: '/achievements', element: <Achievements /> },
      { path: '/me', element: <Profile /> },
      { path: '/vocabulary', element: <Vocabulary /> },
      { path: '/skills', element: <Skills /> },
      { path: '/incentive', element: <Incentive /> },
      { path: '/plans', element: <Plans /> },
      { path: '/corrections', element: <Corrections /> },
      { path: '/parent', element: <Parent /> },
    ]
  },
  {
    path: '/admin',
    element: <AdminShell />,
    children: [
      { index: true, element: <Admin /> },
      { path: 'users', element: <Admin /> },
      { path: 'scenes', element: <Admin /> },
      { path: 'review', element: <Admin /> },
      { path: 'settings', element: <Admin /> },
      { path: 'audit', element: <Admin /> },
    ]
  }
]);

