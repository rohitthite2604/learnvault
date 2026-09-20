import { Component, inject, OnInit, signal } from '@angular/core';
import { ChatResponse, ConversationResponse, MessageResponse } from './chat.model';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { ChatService } from './chat.service';
import { distinctUntilChanged, finalize, map } from 'rxjs';

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
  private readonly router = inject(Router);

  knowledgeSpaceId = signal<number | null>(null);
  conversationId = signal<number | null>(null);

  messages = signal<ChatMessage[]>([]);

  message = signal('');
  sending = signal(false);

  conversations = signal<ConversationResponse[]>([]);
  loadingConversations = signal(false);
  loadingMessages = signal(false);

  ngOnInit(): void {
    this.route.paramMap
    .pipe(
      map((params) => ({
        knowledgeSpaceId: Number(params.get('knowledgeSpaceId')),
        conversationId: params.get('conversationId')
          ? Number(params.get('conversationId'))
          : null
      })),
      distinctUntilChanged(
        (previous, current) =>
          previous.knowledgeSpaceId === current.knowledgeSpaceId &&
          previous.conversationId === current.conversationId
      )
    )
    .subscribe(({ knowledgeSpaceId, conversationId }) => {
      if (!Number.isFinite(knowledgeSpaceId) || knowledgeSpaceId <= 0) {
        return;
      }

      this.knowledgeSpaceId.set(knowledgeSpaceId);
      this.conversationId.set(conversationId);

      this.loadConversations();

      if (conversationId !== null) {
        this.loadMessagesForConversation(conversationId);
      } else {
        this.messages.set([]);
      }
    });
  }

  private loadKnowledgeSpace(): void {
    const id = this.route.snapshot.paramMap.get('knowledgeSpaceId');

    if (!id) { return; }

    this.knowledgeSpaceId.set(Number(id));

    this.loadConversations();
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

        const isNewConversation = this.conversationId() === null;

      this.conversationId.set(response.conversationId);

      this.messages.update(messages => [...messages, {
        role: 'assistant',
        content: response.answer,
        sources: this.deduplicateSources(response.sources)
      }]);

      this.sending.set(false);

      if (isNewConversation) {
        this.router.navigate([
          '/chat',
          this.knowledgeSpaceId(),
          response.conversationId
        ]);
      }

      this.loadConversations();

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

  loadConversations(): void {
    const knowledgeSpaceId = this.knowledgeSpaceId();

    if (knowledgeSpaceId === null) { return; }

    this.loadingConversations.set(true);

    this.chatService.getConversations(knowledgeSpaceId).subscribe({
      next: (conversations) => {
        this.conversations.set(conversations);
        this.loadingConversations.set(false);
      },
      error: (error) => {
        console.error('Failed to load conversations', error);
        this.loadingConversations.set(false);
      }
    });
  }

  openConversation(conversation: ConversationResponse): void {
    this.router.navigate([
      '/chat',
      this.knowledgeSpaceId(),
      conversation.id
    ]);
  }

  newChat(): void {
    this.router.navigate([
      '/chat',
      this.knowledgeSpaceId()
    ]);
  }

  private deduplicateSources(sources: ChatResponse['sources']): ChatResponse['sources'] {
    const seen = new Set<string>();

    return sources.filter((source) => {
      const key = `${source.documentId}-${source.pageNumber}`;

      if (seen.has(key)) { return false; }

      seen.add(key);
      return true;
    });
  }

  private loadMessagesForConversation(conversationId: number): void {
    this.messages.set([]);
    this.loadingMessages.set(true);

    this.chatService.getMessages(conversationId)
      .pipe(
        finalize(() => this.loadingMessages.set(false))
      )
      .subscribe({
        next: (messages: MessageResponse[]) => {
          this.messages.set(
            messages.map((message) => ({
              role: message.role === 'USER' ? 'user' : 'assistant',
              content: message.content
            }))
          );
        },
        error: (error) => {
          console.error('Failed to load conversation messages', error);
        }
      });
  }
  
}
