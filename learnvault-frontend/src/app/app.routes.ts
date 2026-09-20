import { Routes } from '@angular/router';
import { KnowledgeSpaces } from './features/knowledge-spaces/knowledge-spaces';
import { Documents } from './features/documents/documents';
import { Chat } from './features/chat/chat';
import { Layout } from './shared/layout/layout';

export const routes: Routes = [
    {
    path: '',
    component: Layout,
    children: [
      {
        path: '',
        redirectTo: 'knowledge-spaces',
        pathMatch: 'full'
      },
      {
        path: 'knowledge-spaces',
        component: KnowledgeSpaces
      },
      {
        path: 'documents/:knowledgeSpaceId',
        component: Documents
      },
      {
        path: 'chat/:knowledgeSpaceId',
        component: Chat
      }
    ]
  }
];
