import React from 'react';
import { render } from '@testing-library/react';
import Elodizio from './Elodizio';

test('renders learn react link', () => {
  const { getByText } = render(<Elodizio />);
  const linkElement = getByText(/learn react/i);
  expect(linkElement).toBeInTheDocument();
});
