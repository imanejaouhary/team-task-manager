import ClientSpace from './ClientSpace';

export default function Dashboard({ user, onLogout }) {
  let content;
  if (user.role === 'CLIENT') {
    content = <ClientSpace />;
  } else {
    content = <p>L'espace {user.role} n'est pas encore construit.</p>;
  }

  return (
    <div style={{ maxWidth: 640, margin: '40px auto' }}>
      <h1>Bonjour {user.firstName} {user.lastName}</h1>
      <p>Rôle : <strong>{user.role}</strong></p>
      <button onClick={onLogout}>Se déconnecter</button>
      <hr />
      {content}
    </div>
  );
}