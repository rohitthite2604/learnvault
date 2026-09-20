import { Component, inject, OnInit, signal } from '@angular/core';
import { ChatResponse } from './chat.model';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { ChatService } from './chat.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  sources?: ChatResponse['sources'];
}

@Component({
  imports: [
    MatButtonModule,
    MatIconModule
  ],
  selector: 'app-chat',
  styleUrl: './chat.css',
  templateUrl: './chat.html',
})
export class Chat implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly chatService = inject(ChatService);

  knowledgeSpaceId = signal<number | null>(null);
  conversationId = signal<number | null>(null);

  messages = signal<ChatMessage[]>([]);

  message = signal('');
  sending = signal(false);

  ngOnInit(): void {
    this.loadKnowledgeSpace();
  }

  private loadKnowledgeSpace(): void {
    const id = this.route.snapshot.paramMap.get('knowledgeSpaceId');

    if (!id) { return; }

    this.knowledgeSpaceId.set(Number(id));
  }

  sendMessage(): void {

    const text = this.message().trim();

    if (!text || this.sending()) { return; }

    const knowledgeSpaceId = this.knowledgeSpaceId();

    if (knowledgeSpaceId === null ) { return; }

    this.messages.update(messages => [
      ...messages,
      {
        role: 'user',
        content: text
      }
    ]);

    this.message.set('');
    this.sending.set(true);

    this.chatService.sendMessage({
      conversationId: this.conversationId(),
      knowledgeSpaceId,
      message: text
    }).subscribe({

      next: (response) => {

        this.conversationId.set(response.conversationId);

        this.messages.update(messages => [
          ...messages,
          {
            role: 'assistant',
            content: response.answer,
            sources: response.sources
          }
        ]);

        this.sending.set(false);
      },

      error: (error) => {

        console.error('Failed to send message', error);

        this.messages.update(messages => [
          ...messages,
          {
            role: 'assistant',
            content: 'Something went wrong while processing your question.'
          }
        ]);

        this.sending.set(false);
      }
    });
  }

  onMessageChange(event: Event): void {
    const input = event.target as HTMLTextAreaElement;

    this.message.set(input.value);
  }
  
}
