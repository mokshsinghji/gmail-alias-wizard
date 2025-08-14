import { useAliases } from '@/hooks/useAliases';
import { useUser } from '@/hooks/useUser';
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useEffect } from 'react';

export const Route = createFileRoute('/dashboard')({
  component: RouteComponent,
})

function RouteComponent() {
  const { aliases, isLoading, isError, /* addAlias */ } = useAliases();
  const { user, isLoading: isUserLoading, error: userError } = useUser();
  const navigate = useNavigate();
  // const [creatingAlias, setCreatingAlias] = useState(false);
  // const [newAlias, setNewAlias] = useState('');
  // const [newAliasName, setNewAliasName] = useState('');

  useEffect(() => {
    if (!user && !isUserLoading) {
      // handle case when user isn't logged in
      navigate({
        to: '/'
      });
    }

    // setNewAliasName(user?.name || '');
  }, [user, isUserLoading])

  if (isLoading || isUserLoading) {
    return <div>Loading...</div>;
  }

  if (isError || userError) {
    return <div>Error loading aliases</div>;
  }

  console.log(aliases);

  return <div className='flex flex-col p-4'>
    <h2>Current Send-As Aliases</h2>
    {aliases?.length === 0 ? (
      <p>No aliases found</p>
    ) : (
      <ul className='flex flex-col gap-2'>
        {aliases?.map(alias => (
          <li key={alias.sendAsEmail} className='bg-red-300 p-4 max-w-xl flex flex-col'>
            {alias.isDefault && <span className='text-sm text-gray-600'> (default)</span>}
            <span><span className='font-bold'>{alias.displayName || user?.name}:</span> {alias.sendAsEmail}</span>
            {(alias.replyToAddress !== alias.sendAsEmail && alias.replyToAddress == null) && (
              <span className='text-sm text-gray-600'> (reply-to: {alias.replyToAddress})</span>
            )}
          </li>
        ))}
        {/* <li>
          {!creatingAlias && <button className='bg-blue-500 text-white p-2 rounded' onClick={() => {
            setCreatingAlias(true);
          }}>Add Alias</button>}
          {creatingAlias && (
            <div>
              <input type="text" defaultValue={user?.name} value={newAliasName} onChange={e => setNewAliasName(e.target.value)}></input>
              <input type="text" value={newAlias} onChange={(e) => setNewAlias(e.target.value)} />
              
              <button className='bg-blue-500 text-white p-2 rounded' onClick={() => {
                addAlias.mutate({newAlias, newAliasName});
                setCreatingAlias(false);
                setNewAliasName(user?.name || '');
                setNewAlias('');
              }} disabled={addAlias.isPending}>{addAlias.isPending ? 'Creating Alias...' : 'Create Alias'}</button>
            </div>
          )}
        </li> */}
      </ul>
    )}
  </div>;
}
